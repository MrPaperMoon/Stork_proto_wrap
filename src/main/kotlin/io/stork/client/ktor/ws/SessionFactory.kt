package io.stork.client.ktor.ws

import io.stork.client.ktor.DefaultProtobufSerializer
import io.stork.client.ktor.ProtobufSerializer
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class SessionFactory(private val webSocketProvider: WebSocketProvider,
                     private val serializer: ProtobufSerializer = DefaultProtobufSerializer)  {
    private fun createNewSession(webSocket: WebSocket, sessionId: String): WebSocketSession =
        WebSocketSessionImpl(webSocket, serializer, sessionId)

    @OptIn(ExperimentalTime::class)
    suspend fun establishNewSession(address: String, sessionId: String): WebSocketSession {
        val socketEstablishTimeout = Duration.seconds(60)

        return withTimeoutOrNull(socketEstablishTimeout.inWholeMilliseconds) {
            val webSocket = webSocketProvider.startNewSocket(address, sessionId)
            createNewSession(webSocket, sessionId)
        } ?: throw TimeoutException("Not received any system packet within $socketEstablishTimeout timeout")
    }
}