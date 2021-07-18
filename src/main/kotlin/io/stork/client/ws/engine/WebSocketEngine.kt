package io.stork.client.ws.engine

interface WebSocketEngine {
    suspend fun startNewSocket(address: String, sessionId: String?): RawWebSocket
}