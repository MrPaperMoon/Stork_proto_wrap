package io.stork.client.ws

interface WebSocketProvider {
    suspend fun startNewSocket(address: String, sessionId: String?): WebSocket
}