package io.stork.client.ktor.ws

import io.stork.client.ktor.ProtobufSerializer
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory
import java.io.IOException

class WebSocketSessionImpl(private val webSocket: WebSocket,
                           private val serializer: ProtobufSerializer,
                           override val sessionId: String): WebSocketSession {
    private val log = LoggerFactory.getLogger("WS")
    override val isNewSession: Boolean = true

    override val parsedPackets: Flow<WSPacket> = webSocket.received
        .map {
            try {
                val event = serializer.read(WebsocketEvent::class, it)
                WSPacket.Event(event)
            } catch (invalid: IOException) {
                val echo = serializer.read(EchoMessage::class, it)
                WSPacket.Echo(echo)
            }
        }.onEach {
            log.info("--> $it")
        }

    override suspend fun send(payload: EchoMessage) {
        webSocket.send(serializer.write(payload).bytes())
    }

    override suspend fun close() {
        webSocket.close()
    }
}