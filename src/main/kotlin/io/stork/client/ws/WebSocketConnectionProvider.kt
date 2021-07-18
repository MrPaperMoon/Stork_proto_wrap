package io.stork.client.ws

import io.stork.client.ApiClientConfig
import io.stork.client.module.WebSocket
import io.stork.client.ws.engine.WebSocketEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeoutException
import kotlin.time.ExperimentalTime

class WebSocketConnectionProvider(
        private val apiClientConfig: ApiClientConfig,
        private val webSocketEngine: WebSocketEngine
): WebSocketProvider {
    private val log = LoggerFactory.getLogger(WebSocketConnectionProvider::class.java)

    @OptIn(ExperimentalTime::class)
    override suspend fun startWebSocket(sessionId: String): WebSocket {
        val timeout = apiClientConfig.timeout
        val address = apiClientConfig.websocketUrl

        return withTimeoutOrNull(timeout) {
            val webSocket = webSocketEngine.startNewSocket(address, sessionId)
            log.info("Waiting for session info...")
            val notificationSessionInfo = webSocket.received.first().notification_info!!
            log.info("Got session info: {}", notificationSessionInfo)
            WebSocketConnection(sessionId, notificationSessionInfo, webSocket)
        } ?: throw TimeoutException("Not received any system packet within $timeout timeout")
    }
}