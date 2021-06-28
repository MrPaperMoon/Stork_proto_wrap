package io.stork.client.ktor.ws

import io.stork.proto.websocket.EchoMessage
import kotlinx.coroutines.flow.Flow
import okhttp3.Protocol

interface WebSocketSession {
    val sessionId: String
    val isNewSession: Boolean
    val parsedPackets: Flow<WSPacket>
    suspend fun send(payload: EchoMessage)
    suspend fun close()
}