package io.stork.client.ktor.ws

import kotlinx.coroutines.flow.Flow

interface WebSocket {
    val received: Flow<ByteArray>
    suspend fun send(payload: ByteArray)
    suspend fun close()
}