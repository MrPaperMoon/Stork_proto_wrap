package io.stork.client.ktor.ws

import io.stork.proto.websocket.ClientWSPacket
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.ServerWSPacket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.Protocol
import kotlin.time.ExperimentalTime

interface WebSocketSession {
    val sessionId: String
    val lastAckedNotificationId: StateFlow<String>
    val isNewSession: Boolean

    val receivedPackets: Flow<ServerWSPacket>
    suspend fun send(packet: ClientWSPacket)
    suspend fun close()
    fun start(scope: CoroutineScope): Job
}