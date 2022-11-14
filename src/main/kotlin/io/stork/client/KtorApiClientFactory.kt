package io.stork.client

import com.squareup.wire.WireTypeAdapterFactory
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import io.stork.client.ktor.ProtobufFeature
import io.stork.client.okhttp.OkHttpFactory
import io.stork.client.okhttp.OkHttpWebSocketEngine
import io.stork.client.okhttp.Serializers
import io.stork.client.ws.ReconnectingWebSocketProvider

internal object KtorApiClientFactory {
    fun create(config: ApiClientConfig, sessionProvider: SessionProvider): ApiClient {
        val client = OkHttpFactory.createOkHttp(config, sessionProvider)
        val ktorEngine = OkHttp.create {
            preconfigured = client
        }
        val ktorClient = HttpClient(ktorEngine) {
            install(WebSockets) {
                pingInterval = 30_000
            }
            expectSuccess = false
            HttpResponseValidator {
                validateResponse {} // always ok - we handle errors manually due to receive() call recursion
            }
            when (config.mediaType) {
                ApiMediaType.PROTOBUF -> install(ProtobufFeature)
                ApiMediaType.JSON -> install(JsonFeature) {
                    serializer = GsonSerializer {
                        registerTypeAdapterFactory(WireTypeAdapterFactory())
                    }
                }
            }
        }
        val webSocketEngine = OkHttpWebSocketEngine(client, config, Serializers())
        val webSocketProvider = ReconnectingWebSocketProvider(config, webSocketEngine)
        return KtorApiClient(config, ktorClient, sessionProvider, webSocketProvider)
    }
}