package io.stork.client.ws

import io.stork.client.ApiClientConfig
import io.stork.client.ws.engine.WebSocketEngine

fun ReconnectingWebSocketProvider(apiClientConfig: ApiClientConfig,
                                  webSocketEngine: WebSocketEngine): WebSocketProvider {
    return ReconnectingWebSocketProvider(
            apiClientConfig,
            SingleWebSocketProvider(apiClientConfig, webSocketEngine)
    )
}

fun SingleWebSocketProvider(apiClientConfig: ApiClientConfig, webSocketEngine: WebSocketEngine): WebSocketProvider {
    return WebSocketConnectionProvider(apiClientConfig, webSocketEngine)
}