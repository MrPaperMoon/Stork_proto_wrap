package io.stork.client.ws

import io.stork.client.ApiClientConfig
import io.stork.client.CloseReason
import io.stork.client.exceptions.ConnectionClosedException
import io.stork.client.module.WebSocket
import io.stork.client.util.BackOffTimer
import io.stork.client.util.ExponentialBackOffTimer
import io.stork.client.util.takeUntil
import io.stork.client.util.takeWhile
import io.stork.proto.notification.Notification
import io.stork.proto.websocket.Echo
import io.stork.proto.websocket.NotificationAck
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import java.lang.Exception
import kotlin.time.ExperimentalTime

class AutoReconnectingWebSocket(
        override val sessionId: String,
        private val apiClientConfig: ApiClientConfig,
        private val underlyingWebSocketProvider: WebSocketProvider,
): WebSocket {
    private val log = LoggerFactory.getLogger(AutoReconnectingWebSocket::class.java)
    private val currentWebSocketScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override val closeReason: MutableStateFlow<CloseReason?> = MutableStateFlow(null)

    private val currentWebSocket: MutableStateFlow<WebSocket?> = MutableStateFlow(null)
    private val connectedWebSocket: Flow<WebSocket> = currentWebSocket.takeWhile(closeReason) {
        it == null
    }.filterNotNull()

    @OptIn(ExperimentalTime::class)
    private val connectionEstablishingJob: Job = currentWebSocketScope.launch {
        val reconnectTimer: BackOffTimer = ExponentialBackOffTimer()
        while (isActive) {
            try {
                val newWebSocket = underlyingWebSocketProvider.startWebSocket(sessionId)
                currentWebSocket.value = newWebSocket
                reconnectTimer.reset()
                val closeReason = newWebSocket.closeReason.filterNotNull().first()
                if (closeReason is CloseReason.ExceptionalClose) {
                    throw closeReason.cause
                }
            } catch (ex: CancellationException) {
                log.debug("Coroutine cancelled, closing last session... quitting the session creation loop")
                currentWebSocket.getAndUpdate { null }?.close()
                break
            } catch (ex: Exception) {
                log.error("Unknown session error: ", ex)
                val retryTimeout = reconnectTimer.nextTimeout()
                log.info("Will retry after timeout: {}", retryTimeout)
                delay(retryTimeout.inWholeMilliseconds)
            }
        }
    }

    override val isNewSession: Flow<Boolean> =
            connectedWebSocket.flatMapLatest { it.isNewSession }
    override val lastAckReceivedByServer: Flow<String?> =
            connectedWebSocket.flatMapLatest { it.lastAckReceivedByServer }

    override suspend fun sendEcho(echo: Echo) {
        return withConnectedWebSocket {
            sendEcho(echo)
        }
    }

    override suspend fun sendNotificationAck(notificationAck: NotificationAck) {
        return withConnectedWebSocket {
            sendNotificationAck(notificationAck)
        }
    }

    override val receiveEcho: Flow<Echo> =
            connectedWebSocket.flatMapLatest { it.receiveEcho }


    override val notifications: Flow<Notification> =
            connectedWebSocket.flatMapLatest { it.notifications }

    override suspend fun close() {
        if (closeReason.compareAndSet(null, CloseReason.GracefulClose)) {
            currentWebSocketScope.cancel()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun withConnectedWebSocket(receiver: suspend WebSocket.() -> Unit) {
        return withTimeout(apiClientConfig.timeout) {
            closeReason.value?.let {
                throw ConnectionClosedException(closedReason = it, message = "Connection is already closed due to $it")
            }
            val webSocket = connectedWebSocket.first()
            receiver(webSocket)
        }
    }
}