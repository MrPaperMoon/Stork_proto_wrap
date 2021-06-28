package io.stork.client.ktor.ws

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong

class KtorWebSocketProvider(private val client: HttpClient): WebSocketProvider {
    private val socketCounter = AtomicLong(0)

    override suspend fun startNewSocket(address: String, sessionId: String?): WebSocket {
        val realAddress = when (sessionId) {
            null -> address
            else -> "$address?sessionId=$sessionId"
        }

        val log = LoggerFactory.getLogger("WS[${socketCounter.incrementAndGet()}]")

        log.info("<-- establishing session")
        val session = client.webSocketSession {
            url(realAddress)
        }
        log.info("<-- session established -->")

        return object: WebSocket {
            private val incomingFrames = session.incoming.broadcast()

            override val received: Flow<ByteArray>
                get() = incomingFrames.asFlow().map { it.readBytes() }

            override suspend fun send(payload: ByteArray) {
                log.debug("{} <<< {}", address, payload)
                session.send(payload)
            }

            override suspend fun close() {
                log.debug("{} will be closed", address)
                session.close(CloseReason(CloseReason.Codes.NORMAL, "Bye"))
            }
        }

    }
}