package io.stork.client.okhttp

import io.stork.client.ApiClientConfig
import io.stork.client.ApiMediaType
import io.stork.client.ws.engine.RawWebSocket
import io.stork.client.ws.engine.WebSocketEngine
import io.stork.proto.client.websocket.ClientWSPacket
import io.stork.proto.client.websocket.ServerWSPacket
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import okio.ByteString.Companion.toByteString
import org.slf4j.LoggerFactory

class OkHttpWebSocketEngine(
    private val okHttpClient: OkHttpClient,
    private val apiClientConfig: ApiClientConfig,
    private val serializers: Serializers,
    private val webSocketListener: WebSocketListener? = null,
) : WebSocketEngine {
    private val socketCounter = AtomicLong(0)

    override suspend fun startNewSocket(address: String, sessionId: String?): RawWebSocket =
        startNewSocketImpl(address, sessionId)

    private fun startNewSocketImpl(address: String, sessionId: String?): RawWebSocket {
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
        val packetsReceiver = IncomingDataFramesListener(serializers) {
            val unknownFields = it.unknownFields
                .takeIf { it.size > 0 }?.base64()?.let { " + ??? [$it]" } ?: ""
            log.info("{} --> {}{}", address, it, unknownFields)
        }

        log.info("Opening new websocket, address = {}", realAddress)
        val backingWebSocket =
            okHttpClient.newWebSocket(socketRequest, CompositeWebSocketListener(logger, debugger, packetsReceiver))
        log.info("Websocket opened")

        return object : RawWebSocket {
            override val received: Flow<ServerWSPacket> = packetsReceiver.packets

            override suspend fun send(payload: ClientWSPacket) {
                log.info("{} <-- {}", address, payload)
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
