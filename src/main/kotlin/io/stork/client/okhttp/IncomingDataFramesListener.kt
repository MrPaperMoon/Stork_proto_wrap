package io.stork.client.okhttp

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class IncomingDataFramesListener: WebSocketListener() {
    private val packetsChannel: BroadcastChannel<ByteArray> = BroadcastChannel(Channel.CONFLATED)

    val packets: Flow<ByteArray> = packetsChannel.asFlow()

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        if (!packetsChannel.isClosedForSend) {
            packetsChannel.offer(bytes.toByteArray())
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        complete()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        complete()
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
        complete()
    }

    private fun complete() {
        if (!packetsChannel.isClosedForSend) {
            packetsChannel.close()
        }
    }
}