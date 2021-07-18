package io.stork.client.ws

import io.stork.client.module.WebSocket

interface WebSocketProvider {
    suspend fun startWebSocket(sessionId: String): WebSocket
}
