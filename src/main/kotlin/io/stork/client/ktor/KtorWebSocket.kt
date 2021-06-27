package io.stork.client.ktor

import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.Message
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import io.stork.client.ApiClientConfig
import io.stork.client.LogLevel
import io.stork.client.module.EventWebsocket
import io.stork.client.util.repeat
import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import java.io.IOException

class KtorWebSocket(
    private val config: ApiClientConfig,
    private val client: HttpClient,
    private val serializer: ProtobufSerializer = DefaultProtobufSerializer
): EventWebsocket {
    private val scope = GlobalScope
    private val log = LoggerFactory.getLogger("WS")

    private val webSocketConnection: SharedFlow<WebSocketSession> = flow {
        var connection: WebSocketSession? = null
        while (connection == null || !connection.isActive) {
            connection = newWebSocketSession()
            if (connection != null) {
                emit(connection)
                while (connection.isActive) {
                    delay(1_000)
                }
            }
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(), replay = 1)

    private val webSocketFrames: SharedFlow<Frame> = webSocketConnection.flatMapConcat { it.receivedFrames }
        .repeat()
        .shareIn(scope, SharingStarted.WhileSubscribed(), replay = 0)

    override val allEvents: Flow<WebsocketEvent> = webSocketFrames.mapNotNull {
        val event = it.tryRead<WebsocketEvent>()
        if (event != null && config.logLevel == LogLevel.BODY) {
            log.info("--> $event")
        }
        event
    }

    override val conferenceEvents: Flow<ConferenceEvent> = allEvents.getEvents(WebsocketEvent.EventCase.CONFERENCE_EVENT) {
        it.conferenceEvent
    }

    override val webRTCEvents: Flow<RTCEvent> = allEvents.getEvents(WebsocketEvent.EventCase.RTC_EVENT) {
        it.rtcEvent
    }

    override suspend fun sendEcho(echo: EchoMessage): Boolean {
        val webSocket = webSocketConnection.first()
        webSocket.send(serializer.write(echo).bytes())
        return true
    }

    override val receiveEcho: Flow<EchoMessage> = webSocketFrames.mapNotNull {
        it.tryRead()
    }

    private fun <T> Flow<WebsocketEvent>.getEvents(type: WebsocketEvent.EventCase, selector: (WebsocketEvent) -> T): Flow<T> {
        return mapNotNull {
            if (it.eventCase == type) {
                selector(it)
            } else null
        }
    }

    private val WebSocketSession.receivedFrames: Flow<Frame>
        get() = incoming.consumeAsFlow()

    private inline fun <reified T: Message> Frame.tryRead(): T? = try {
        serializer.read(T::class, readBytes())
    } catch (ex: InvalidProtocolBufferException) {
        null
    }

    private suspend fun newWebSocketSession(): DefaultClientWebSocketSession? {
        try {
            if (config.logLevel >= LogLevel.BASIC) {
                log.info("<-- establishing session")
            }
            val session = client.webSocketSession {
                url(config.websocketUrl)
            }
            if (config.logLevel >= LogLevel.BASIC) {
                log.info("<-- session established -->")
            }

            return session
        } catch (ex: IOException) {
            // reconnect
            if (config.logLevel >= LogLevel.BASIC) {
                log.info("--X session establishing failure: ", ex)
            }
            return null
        }
    }
}