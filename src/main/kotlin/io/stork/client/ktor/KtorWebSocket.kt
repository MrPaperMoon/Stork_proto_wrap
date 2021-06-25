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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive

class KtorWebSocket(
    private val config: ApiClientConfig,
    private val client: HttpClient,
    private val serializer: ProtobufSerializer = DefaultProtobufSerializer
): EventWebsocket {
    private val scope = GlobalScope

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
            emit(event)
        }
    }

    override fun conferenceEvent(): Flow<ConferenceEvent> = webSocketEvent.mapNotNull {
        it.conferenceEvent
    }

    override fun webRTCEvent(): Flow<RTCEvent> = webSocketEvent.mapNotNull {
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
}