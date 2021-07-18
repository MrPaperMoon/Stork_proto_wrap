package io.stork.client.ws.engine

import io.stork.proto.websocket.ClientWSPacket
import io.stork.proto.websocket.ServerWSPacket
import kotlinx.coroutines.flow.Flow

interface RawWebSocket {
    val received: Flow<ServerWSPacket>
    suspend fun send(payload: ClientWSPacket)
    suspend fun close()
}