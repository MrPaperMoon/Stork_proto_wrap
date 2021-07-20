package io.stork.client

import io.stork.proto.notification.Notification
import io.stork.proto.websocket.Echo
import io.stork.proto.websocket.NotificationAck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WebSocket {
    val sessionId: String
    val isNewSession: Flow<Boolean>
    val lastAckReceivedByServer: Flow<String?>

    suspend fun sendEcho(echo: Echo)
    val receiveEcho: Flow<Echo>

    suspend fun sendNotificationAck(notificationAck: NotificationAck)

    val notifications: Flow<Notification>

    val closeReason: StateFlow<CloseReason?>
    suspend fun close()
}