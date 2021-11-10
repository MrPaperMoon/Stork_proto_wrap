package io.stork.client

import io.stork.proto.client.notifications.Notification
import io.stork.proto.client.websocket.Echo
import io.stork.proto.client.websocket.NotificationAck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WebSocket: ApiNotificationsSource {
    val sessionId: String
    val isNewSession: Flow<Boolean>
    val lastAckReceivedByServer: Flow<String?>

    suspend fun sendEcho(echo: Echo)
    val receiveEcho: Flow<Echo>

    suspend fun sendNotificationAck(notificationAck: NotificationAck)

    override val notifications: Flow<Notification>

    val closeReason: StateFlow<CloseReason?>
    suspend fun close()
}
