package io.stork.client.ktor.ws

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import io.stork.client.ApiClientConfig
import io.stork.client.ApiMediaType
import io.stork.client.okhttp.Serializers
import io.stork.client.ws.engine.RawWebSocket
import io.stork.client.ws.engine.WebSocketEngine
import io.stork.proto.client.websocket.ClientWSPacket
import io.stork.proto.client.websocket.ServerWSPacket
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.slf4j.LoggerFactory

class KtorWebSocketEngine(private val client: HttpClient,
                          private val apiClientConfig: ApiClientConfig,
                          private val serializers: Serializers) : WebSocketEngine {
    private val socketCounter = AtomicLong(0)

    override suspend fun startNewSocket(address: String, sessionId: String?): RawWebSocket {
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

        return object : RawWebSocket {
            private val incomingFrames = session.incoming.broadcast()

            override val received: Flow<ServerWSPacket>
                get() = incomingFrames.asFlow().mapNotNull {
                    val packet = when (it) {
                        is Frame.Binary -> serializers.protobufSerializer.read(ServerWSPacket::class, it.readBytes())
                        is Frame.Text -> serializers.gson.fromJson(it.readText(), ServerWSPacket::class.java)
                        is Frame.Close -> {
                            handleRemoteClose(closeReason = it.readReason())
                            null
                        }
                        else -> null
                    }
                    packet?.also {
                        log.debug("{} >>> {}", address, it)
                    }
                }

            @OptIn(ObsoleteCoroutinesApi::class)
            private suspend fun handleRemoteClose(closeReason: CloseReason?) {
                when {
                    closeReason == null -> incomingFrames.cancel()
                    closeReason.code == 1000.toShort() -> incomingFrames.cancel()
                    else -> {
                        log.error("{} closed session with $closeReason")
                        incomingFrames.close(IllegalStateException("Unexpected close reason: $closeReason"))
                    }
                }
            }

            override suspend fun send(payload: ClientWSPacket) {
                log.debug("{} <<< {}", address, payload)
                when (apiClientConfig.mediaType) {
                    ApiMediaType.PROTOBUF -> session.send(serializers.protobufSerializer.write(payload).bytes())
                    ApiMediaType.JSON -> session.send(serializers.gson.toJson(payload))
                }
            }

            override suspend fun close() {
                log.debug("{} will be closed", address)
                session.close(CloseReason(CloseReason.Codes.NORMAL, "Bye"))
            }
        }

    }
}
