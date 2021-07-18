package io.stork.client

import com.squareup.wire.WireTypeAdapterFactory
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import io.stork.client.ktor.ProtobufFeature
import io.stork.client.ktor.StorkKtorResponseValidator
import io.stork.client.okhttp.OkHttpWebSocketEngine
import io.stork.client.okhttp.OkHttpFactory
import io.stork.client.okhttp.Serializers
import io.stork.client.ws.ReconnectingWebSocketProvider
import io.stork.client.ws.WebSocketConnectionProvider
import io.stork.client.ws.WebSocketProvider
import io.stork.client.ws.engine.WebSocketEngine

internal object KtorApiClientFactory {
    fun create(config: ApiClientConfig, sessionManager: SessionManager): ApiClient {
        val client = OkHttpFactory.createOkHttp(config, sessionManager)
        val ktorEngine = OkHttp.create {
            preconfigured = client
        }
        val ktorClient = HttpClient(ktorEngine) {
            install(WebSockets) {
                pingInterval = 30_000
            }
            StorkKtorResponseValidator()
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
        return KtorApiClient(config, ktorClient, sessionManager, webSocketProvider)
    }
}