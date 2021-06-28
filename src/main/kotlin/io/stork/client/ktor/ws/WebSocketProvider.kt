package io.stork.client.ktor.ws

interface WebSocketProvider {
    suspend fun startNewSocket(address: String, sessionId: String?): WebSocket
}