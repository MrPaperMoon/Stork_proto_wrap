package io.stork.client.ws

import io.stork.client.ApiClientConfig
import io.stork.client.CloseReason
import io.stork.client.WebSocket
import io.stork.client.WebSocketState
import io.stork.client.exceptions.ConnectionClosedException
import io.stork.client.util.BackOffTimer
import io.stork.client.util.ExponentialBackOffTimer
import io.stork.client.util.takeWhile
import io.stork.proto.client.notifications.Notification
import io.stork.proto.client.websocket.Echo
import io.stork.proto.client.websocket.NotificationAck
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory

class AutoReconnectingWebSocket(
    override val sessionId: String,
    private val apiClientConfig: ApiClientConfig,
    private val underlyingWebSocketProvider: WebSocketProvider,
    private val webSocketScope: CoroutineScope = CoroutineScope(SupervisorJob())
) : WebSocket {
    private val log = LoggerFactory.getLogger(AutoReconnectingWebSocket::class.java)
    override val closeReason: MutableStateFlow<CloseReason?> = MutableStateFlow(null)

    private val currentWebSocket: MutableStateFlow<WebSocket?> = MutableStateFlow(null)
    private val connectedWebSocket: Flow<WebSocket> = currentWebSocket.takeWhile(closeReason) {
        it == null
    }.filterNotNull()

    private val _state: MutableStateFlow<WebSocketState> = MutableStateFlow(WebSocketState.DISCONNECTED)
    override val state: StateFlow<WebSocketState> = _state.asStateFlow()

    @OptIn(ExperimentalTime::class)
    private val connectionEstablishingJob: Job = webSocketScope.launch {
        withContext(Dispatchers.IO) {
            val reconnectTimer: BackOffTimer = ExponentialBackOffTimer()
            while (isActive) {
                try {
                    _state.value = WebSocketState.CONNECTING
                    val newWebSocket = underlyingWebSocketProvider.startWebSocket(sessionId)
                    currentWebSocket.value = newWebSocket
                    log.info("WS: established")
                    _state.value = WebSocketState.CONNECTED
                    reconnectTimer.reset()
                    val closeReason = newWebSocket.closeReason.filterNotNull().first()
                    if (closeReason is CloseReason.ExceptionalClose) {
                        log.info("WS: closed, reason: {}", closeReason)
                        throw closeReason.cause
                    }
                } catch (ex: CancellationException) {
                    log.debug("WS: Coroutine cancelled, closing last session... quitting the session creation loop")
                    currentWebSocket.getAndUpdate { null }?.close()
                    _state.value = WebSocketState.DISCONNECTED
                    break
                } catch (ex: Exception) {
                    log.error("WS: Unknown session error: ", ex)
                    val retryTimeout = reconnectTimer.nextTimeout()
                    log.info("WS: Will retry after timeout: {}", retryTimeout)
                    _state.value = WebSocketState.DISCONNECTED
                    delay(retryTimeout.inWholeMilliseconds)
                }
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
            webSocketScope.cancel()
            _state.value = WebSocketState.DISCONNECTED
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun withConnectedWebSocket(receiver: suspend WebSocket.() -> Unit) {
        return withTimeout(apiClientConfig.timeout) {
            closeReason.value?.let {
                throw ConnectionClosedException(closedReason = it, message = "Connection is already closed due to $it")
            }
            val webSocket = connectedWebSocket.firstOrNull()
            webSocket?.let { receiver(it) }
        }
    }
}
