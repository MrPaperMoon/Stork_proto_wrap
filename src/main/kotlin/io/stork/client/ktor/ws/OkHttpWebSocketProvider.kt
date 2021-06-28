package io.stork.client.ktor.ws

import io.stork.client.okhttp.CompositeWebSocketListener
import io.stork.client.okhttp.IncomingDataFramesListener
import io.stork.client.okhttp.LoggingWebSocketListener
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import okio.ByteString.Companion.toByteString
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong

class OkHttpWebSocketProvider(
    private val okHttpClient: OkHttpClient,
    private val webSocketListener: WebSocketListener? = null
): WebSocketProvider {
    private val socketCounter = AtomicLong(0)

    override suspend fun startNewSocket(address: String, sessionId: String?): WebSocket =
            startNewSocketImpl(address, sessionId)

    private fun startNewSocketImpl(address: String, sessionId: String?): WebSocket {
        val realAddress = when (sessionId) {
            null -> address
            else -> "$address?sessionId=$sessionId"
        }

        val log = LoggerFactory.getLogger("WS[${socketCounter.incrementAndGet()}]")

        val socketRequest = Request.Builder()
                .get()
                .url(realAddress)
                .build()

        val logger = LoggingWebSocketListener(address, log)
        val debugger = webSocketListener
        val packetsReceiver = IncomingDataFramesListener()

        val backingWebSocket =
                okHttpClient.newWebSocket(socketRequest, CompositeWebSocketListener(logger, debugger, packetsReceiver))

        return object: WebSocket {
            override val received: Flow<ByteArray>
                get() = packetsReceiver.packets

            override suspend fun send(payload: ByteArray) {
                log.debug("{} <<< {}", address, payload)
                backingWebSocket.send(payload.toByteString())
            }

            override suspend fun close() {
                log.debug("{} will be closed", address)
                backingWebSocket.close(1000, "Bye")
            }
        }
    }
}