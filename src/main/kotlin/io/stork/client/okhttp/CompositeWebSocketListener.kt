package io.stork.client.okhttp

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class CompositeWebSocketListener private constructor(private val child: List<WebSocketListener>): WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        child.forEach { it.onOpen(webSocket, response) }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        child.forEach { it.onClosing(webSocket, code, reason) }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        child.forEach { it.onMessage(webSocket, bytes) }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        child.forEach { it.onMessage(webSocket, text) }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        child.forEach { it.onClosed(webSocket, code, reason) }
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
        child.forEach { it.onFailure(webSocket, throwable, response) }
    }


    companion object {
        operator fun invoke(vararg listener: WebSocketListener?): WebSocketListener =
                CompositeWebSocketListener(listener.toList().filterNotNull())
    }
}