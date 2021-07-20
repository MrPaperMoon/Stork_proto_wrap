package io.stork.client.ws

import io.stork.client.ApiClientConfig
import io.stork.client.WebSocket

class ReconnectingWebSocketProvider(
        private val apiClientConfig: ApiClientConfig,
        private val underlyingWebSocketProvider: WebSocketProvider
): WebSocketProvider {
    override suspend fun startWebSocket(sessionId: String): WebSocket {
        return AutoReconnectingWebSocket(sessionId, apiClientConfig, underlyingWebSocketProvider)
    }
}