package io.stork.client.ktor

import io.stork.client.ApiClientConfig
import io.stork.client.SessionManager
import io.stork.client.ktor.ws.WebSocketSession
import io.stork.client.ktor.ws.WebSocketSessionFactory
import io.stork.client.module.Websocket
import io.stork.client.util.launchCatching
import io.stork.proto.notification.Notification
import io.stork.proto.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class WebSocketImpl(
    private val config: ApiClientConfig,
    private val sessionManager: SessionManager,
    private val webSocketSessionFactory: WebSocketSessionFactory
): Websocket {
    private val log = LoggerFactory.getLogger("WS")
    private val watcherScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        sessionManager.sessionTokenChangedSignal.connect {
            if (it == null) stop()
            else start(config.websocketUrl, it)
        }
    }

    private val session: MutableStateFlow<WebSocketSession?> = MutableStateFlow(null)
    private val onSessionMissImpl = MutableSharedFlow<Unit>()

    private var sessionSubscription: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var sessionStartSubscription: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private var wsConnectionParams: WSConnectionParameters? = null
        set(value) {
            when {
                value == null -> {
                    log.debug("Drop session")
                    field = null
                    sessionSubscription = null
                    sessionStartSubscription = null
                    session.value = null
                }
                field == null -> synchronized(session) {
                    field = value
                    log.debug("starting new session establishment lifecycle, id = {}", value.sessionId)
                    val keksSession = webSocketSessionFactory.newReconnectingSession(value.address, value.sessionId)
                    sessionSubscription = watcherScope.launchCatching {
                        keksSession.collect {
                            session.value = it
                            sessionStartSubscription = it.start(watcherScope)
                            if (it.isNewSession) {
                                onSessionMissImpl.emit(Unit)
                            }
                        }
                    }
                }
                else -> {
                    field = value
                    log.debug("already running some session, ignoring new params: {}", value)
                }
            }
        }


    private fun start(address: String, sessionId: String) {
        log.debug("start client with address = {}, sessionId = {}", address, sessionId)
        wsConnectionParams = WSConnectionParameters(address, sessionId)
    }

    private fun stop() {
        log.debug("stop client")
        wsConnectionParams = null
    }

    override suspend fun sendEcho(echo: Echo) {
        send(ClientWSPacket(echo = echo))
    }

    private val packetsStream: Flow<ServerWSPacket> = session
            .filterNotNull()
            .flatMapConcat { it.receivedPackets }
            .catch {}

    override val receiveEcho: Flow<Echo> = packetsStream
        .mapNotNull {
            it.echo
        }


    override val notifications: Flow<Notification> = packetsStream
        .mapNotNull {
            it.notification
        }

    @OptIn(ExperimentalTime::class)
    private suspend fun send(packet: ClientWSPacket) {
        return withTimeout(Duration.minutes(1)) {
            val currentSession = session.filterNotNull().first()
            currentSession.send(packet)
        }
    }
}