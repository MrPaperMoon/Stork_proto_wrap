package io.stork.client.ktor.ws

import kotlin.time.ExperimentalTime

interface SessionFactory<T> {
    suspend fun establishNewSession(address: String, sessionId: String): T
}