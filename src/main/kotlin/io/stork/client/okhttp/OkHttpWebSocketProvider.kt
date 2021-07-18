package io.stork.client.okhttp

import io.stork.client.ApiClientConfig
import io.stork.client.ApiMediaType
import io.stork.client.ws.WebSocket
import io.stork.client.ws.WebSocketProvider
import io.stork.proto.websocket.ClientWSPacket
import io.stork.proto.websocket.ServerWSPacket
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import okio.ByteString.Companion.toByteString
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong

class OkHttpWebSocketProvider(
        private val okHttpClient: OkHttpClient,
        private val apiClientConfig: ApiClientConfig,
        private val serializers: Serializers,
        private val webSocketListener: WebSocketListener? = null,
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
        val packetsReceiver = IncomingDataFramesListener(serializers)

        log.info("Opening new websocket, address = {}", realAddress)
        val backingWebSocket =
                okHttpClient.newWebSocket(socketRequest, CompositeWebSocketListener(logger, debugger, packetsReceiver))

        return object: WebSocket {
            override val received: Flow<ServerWSPacket>
                get() = packetsReceiver.packets

            override suspend fun send(payload: ClientWSPacket) {
                log.debug("{} <<< {}", address, payload)
                when (apiClientConfig.mediaType) {
                    ApiMediaType.PROTOBUF -> backingWebSocket.send(serializers.protobufSerializer.write(payload).bytes().toByteString())
                    ApiMediaType.JSON -> backingWebSocket.send(serializers.gson.toJson(payload))
                }
            }

            override suspend fun close() {
                log.info("{} will be closed", address)
                backingWebSocket.close(1000, "Bye")
            }
        }
    }
}