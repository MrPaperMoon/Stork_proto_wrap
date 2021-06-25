package io.stork.client.ktor

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import io.stork.client.ApiClientConfig
import io.stork.client.module.EventWebsocket
import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import org.slf4j.LoggerFactory

class KtorWebSocket(
    private val config: ApiClientConfig,
    private val client: HttpClient,
    private val serializer: ProtobufSerializer = DefaultProtobufSerializer
): EventWebsocket {
    private val scope = GlobalScope
    private val log = LoggerFactory.getLogger("WS")

    private val webSocketConnection: Flow<WebSocketSession> = flow {
        emit(client.webSocketSession {
            url(config.websocketUrl)
        })
    }.shareIn(scope, SharingStarted.WhileSubscribed(), replay = 1)

    private val webSocketEvent: Flow<WebsocketEvent> = flow {
        val webSocket = webSocketConnection.first()
        val context = currentCoroutineContext()
        while (context.isActive) {
            val frame = webSocket.incoming.receive()
            val event = serializer.read(WebsocketEvent::class, frame.readBytes())
            log.info("--> $event")
            emit(event)
        }
    }

    override fun conferenceEvent(): Flow<ConferenceEvent> = webSocketEvent.getEvents(WebsocketEvent.EventCase.CONFERENCE_EVENT) {
        it.conferenceEvent
    }

    override fun webRTCEvent(): Flow<RTCEvent> = webSocketEvent.getEvents(WebsocketEvent.EventCase.RTC_EVENT) {
        it.rtcEvent
    }

    override suspend fun sendEcho(echo: EchoMessage): Boolean {
        val webSocket = webSocketConnection.first()
        webSocket.send(serializer.write(echo).bytes())
        return true
    }

    override fun receiveEcho(): Flow<EchoMessage> {
        TODO("Not yet implemented")
    }

    private fun <T> Flow<WebsocketEvent>.getEvents(type: WebsocketEvent.EventCase, selector: (WebsocketEvent) -> T): Flow<T> {
        return mapNotNull {
            if (it.eventCase == type) {
                selector(it)
            } else null
        }
    }
}