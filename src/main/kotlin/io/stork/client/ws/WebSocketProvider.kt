package io.stork.client.ws

import io.stork.client.WebSocket

interface WebSocketProvider {
    suspend fun startWebSocket(sessionId: String): WebSocket
}
