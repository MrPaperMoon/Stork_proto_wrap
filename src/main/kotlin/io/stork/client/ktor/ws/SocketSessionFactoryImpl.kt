package io.stork.client.ktor.ws

import io.stork.client.ws.WebSocket
import io.stork.client.ws.WebSocketProvider
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class SocketSessionFactoryImpl(private val webSocketProvider: WebSocketProvider) : SessionFactory<WebSocketSession> {
    private suspend fun createNewSession(webSocket: WebSocket, sessionId: String): WebSocketSession =
        WebSocketSessionImpl(webSocket, sessionId)

    @OptIn(ExperimentalTime::class)
    override suspend fun establishNewSession(address: String, sessionId: String): WebSocketSession {
        val socketEstablishTimeout = Duration.seconds(60)

        return withTimeoutOrNull(socketEstablishTimeout.inWholeMilliseconds) {
            val webSocket = webSocketProvider.startNewSocket(address, sessionId)
            createNewSession(webSocket, sessionId)
        } ?: throw TimeoutException("Not received any system packet within $socketEstablishTimeout timeout")
    }
}