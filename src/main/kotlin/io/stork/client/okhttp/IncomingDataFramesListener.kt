package io.stork.client.okhttp

import io.ktor.http.cio.websocket.*
import io.stork.proto.websocket.ServerWSPacket
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.lang.IllegalStateException

class IncomingDataFramesListener(private val serializers: Serializers): WebSocketListener() {
    private val packetsChannel: BroadcastChannel<ServerWSPacket> = BroadcastChannel(Channel.CONFLATED)

    val packets: Flow<ServerWSPacket> = packetsChannel.asFlow()

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        if (!packetsChannel.isClosedForSend) {
            packetsChannel.offer(serializers.protobufSerializer.read(ServerWSPacket::class, bytes.toByteArray()))
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        if (!packetsChannel.isClosedForSend) {
            packetsChannel.offer(serializers.gson.fromJson(text, ServerWSPacket::class.java))
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        complete()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        // called then remote is closing the socket
        if (code == CloseReason.Codes.NORMAL.code.toInt()) {
            complete()
        } else {
            complete(exception = IllegalStateException("Unexpected close code: $code, reason $reason"))
        }
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
        complete(exception = throwable)
    }

    private fun complete(exception: Throwable? = null) {
        if (!packetsChannel.isClosedForSend) {
            packetsChannel.close(exception)
        }
    }
}