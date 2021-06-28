package io.stork.client.ktor.ws

import com.google.protobuf.InvalidProtocolBufferException
import io.stork.client.ktor.ProtobufSerializer
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WebSocketSessionImpl(private val webSocket: WebSocket,
                           private val serializer: ProtobufSerializer,
                           override val sessionId: String): WebSocketSession {
    override val isNewSession: Boolean = true

    override val parsedPackets: Flow<WSPacket> = webSocket.received
        .map {
            try {
                val event = serializer.read(WebsocketEvent::class, it)
                WSPacket.Event(event)
            } catch (invalid: InvalidProtocolBufferException) {
                val echo = serializer.read(EchoMessage::class, it)
                WSPacket.Echo(echo)
            }
        }

    override suspend fun send(payload: EchoMessage) {
        webSocket.send(serializer.write(payload).bytes())
    }

    override suspend fun close() {
        webSocket.close()
    }
}