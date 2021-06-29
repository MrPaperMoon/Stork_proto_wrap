package io.stork.client.okhttp

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.slf4j.Logger

class LoggingWebSocketListener(private val socketAddress: String,
                               private val log: Logger): WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        val responseData = "" +
                response.code + " " + response.message + "\n" +
                response.headers.toMultimap().flatMap { entry ->
                    entry.value.map { entry.key + ": " + it }
                }.joinToString("\n") + "\n" +
                (response.body?.string() ?: "")
        log.debug("{} opened. Response: {}", socketAddress, responseData)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        log.debug("{} closing... Code: {}, reason: {}", socketAddress, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        log.debug("{} closed. Code: {}, reason: {}", socketAddress, code, reason)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        log.debug("{} >>> Bytes: {}", socketAddress, bytes.base64())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        log.debug("{} >>> {}", socketAddress, text)
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
        val responseDescription = response?.let {
            "${it.code}, ${it.message}, body: ${
                it.body?.string()
            }" } ?: "no response!"
        log.error("{} failure: $responseDescription ", socketAddress, throwable)
    }
}