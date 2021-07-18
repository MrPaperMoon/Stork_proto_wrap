package io.stork.client.ktor.ws

import io.stork.client.ws.WebSocket
import io.stork.proto.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class WebSocketSessionImpl(private val webSocket: WebSocket,
                           override val sessionId: String,
                           initialSessionInfo: NotificationSessionInfo,
                           ): WebSocketSession {
    override val isNewSession: Boolean = initialSessionInfo.is_new_connection
    override val receivedPackets: Flow<ServerWSPacket> = webSocket.received

    // TODO send ack
    @OptIn(ExperimentalTime::class)
    val lastReceivedNotificationId = receivedPackets.mapNotNull {
        it.notification?.notification_id
    }.debounce(Duration.seconds(2))

    override val lastAckedNotificationId: MutableStateFlow<String> = MutableStateFlow(initialSessionInfo.last_ack_notification_id)

    override suspend fun send(packet: ClientWSPacket) {
        webSocket.send(packet)
    }

    override suspend fun close() {
        webSocket.close()
    }

    override fun start(scope: CoroutineScope): Job {
        return scope.launch {
            lastReceivedNotificationId.collect {
                send(ClientWSPacket(notification_ack = NotificationAck(it)))
                lastAckedNotificationId.value = it
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger("WS")

        suspend operator fun invoke(webSocket: WebSocket,
                                    sessionId: String): WebSocketSessionImpl {
            log.info("Waiting for session info...")
            val notificationSessionInfo = webSocket.received.first().notification_info!!
            log.info("Got session info: {}", notificationSessionInfo)
            return WebSocketSessionImpl(webSocket, sessionId, notificationSessionInfo)
        }
    }
}