package io.stork.client.ws

import io.stork.client.CloseReason
import io.stork.client.WebSocket
import io.stork.client.exceptions.ConnectionClosedException
import io.stork.client.util.takeWhile
import io.stork.client.ws.engine.RawWebSocket
import io.stork.proto.client.notifications.Notification
import io.stork.proto.client.websocket.*
import kotlinx.coroutines.flow.*

class WebSocketConnection(
        override val sessionId: String,
        notificationSessionInfo: NotificationSessionInfo,
        private val rawWebSocket: RawWebSocket
): WebSocket {
    override val isNewSession: Flow<Boolean> = flowOf(notificationSessionInfo.is_new_connection)
    override val lastAckReceivedByServer: Flow<String?> = flowOf(notificationSessionInfo.last_ack_notification_id)
    override val closeReason: MutableStateFlow<CloseReason?> = MutableStateFlow(null)

    override suspend fun sendEcho(echo: Echo) {
        send(ClientWSPacket(echo = echo))
    }

    override suspend fun sendNotificationAck(notificationAck: NotificationAck) {
        send(ClientWSPacket(notification_ack = notificationAck))
    }

    private suspend fun send(packet: ClientWSPacket) {
        closeReason.value?.let {
            throw ConnectionClosedException(closedReason = it, message = "Connection is already closed due to $it")
        }
        rawWebSocket.send(packet)
    }

    private val receivedPackages: Flow<ServerWSPacket> = rawWebSocket.received.catch {
        closeReason.compareAndSet(null, CloseReason.ExceptionalClose(it))
    }.takeWhile(closeReason) { it == null }

    override val receiveEcho: Flow<Echo> = receivedPackages.mapNotNull { it.echo }
    override val notifications: Flow<Notification> = receivedPackages.mapNotNull { it.notification }

    override suspend fun close() {
        if (closeReason.compareAndSet(null, CloseReason.GracefulClose)) {
            rawWebSocket.close()
        }
    }
}
