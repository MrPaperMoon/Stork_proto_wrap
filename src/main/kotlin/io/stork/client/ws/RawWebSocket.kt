package io.stork.client.ws

import io.stork.proto.client.websocket.ClientWSPacket
import io.stork.proto.client.websocket.ServerWSPacket
import kotlinx.coroutines.flow.Flow

interface RawWebSocket {
    val received: Flow<ServerWSPacket>
    suspend fun send(payload: ClientWSPacket)
    suspend fun close()
}
