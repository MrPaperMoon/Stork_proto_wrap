package io.stork.client

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import io.stork.client.ktor.ProtobufFeature
import io.stork.client.ktor.StorkKtorResponseValidator
import io.stork.client.ktor.WebSocketImpl
import io.stork.client.ktor.ws.KtorWebSocketProvider
import io.stork.client.ktor.ws.OkHttpWebSocketProvider
import io.stork.client.ktor.ws.WebSocketSessionFactory
import io.stork.client.okhttp.OkHttpFactory
import io.stork.client.okhttp.objectMapper

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
                    serializer = JacksonSerializer(objectMapper)
                }
            }
        }
        val websocket = WebSocketImpl(config, sessionManager, WebSocketSessionFactory(
            OkHttpWebSocketProvider(client)
        )
        )
        return KtorApiClient(config, ktorClient, sessionManager, websocket)
    }
}