package io.stork.client.ktor

import io.stork.client.ApiClientConfig
import io.stork.client.SessionManager
import io.stork.client.ktor.ws.WSPacket
import io.stork.client.ktor.ws.WebSocketSession
import io.stork.client.ktor.ws.WebSocketSessionFactory
import io.stork.client.module.EventWebsocket
import io.stork.client.util.launchCatching
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory

class WebSocketImpl(
    private val config: ApiClientConfig,
    private val sessionManager: SessionManager,
    private val webSocketSessionFactory: WebSocketSessionFactory
): EventWebsocket {
    private val log = LoggerFactory.getLogger("WS")
    private val watcherScope: CoroutineScope = GlobalScope + Dispatchers.IO

    init {
        sessionManager.sessionTokenChangedSignal.connect {
            if (it == null) stop()
            else start(config.websocketUrl, it)
        }
    }

    private val session: MutableStateFlow<WebSocketSession?> = MutableStateFlow(null)
    private val onSessionMissImpl = MutableSharedFlow<Unit>()

    private var keksSessionSubscription: Job? = null
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
                    keksSessionSubscription = null
                    session.value = null
                }
                field == null -> synchronized(session) {
                    field = value
                    log.debug("starting new session establishment lifecycle, id = {}", value.sessionId)
                    val keksSession = webSocketSessionFactory.newReconnectingSession(value.address, value.sessionId)
                    keksSessionSubscription = watcherScope.launchCatching {
                        keksSession.collect {
                            session.value = it
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

    override suspend fun sendEcho(echo: EchoMessage): Boolean {
        return when (val currentSession = session.value) {
            null -> false
            else -> {
                currentSession.send(echo)
                true
            }
        }
    }

    private val packetsStream: Flow<WSPacket> = session
        .filterNotNull()
        .flatMapConcat { it.parsedPackets }
        .catch {}

    override val receiveEcho: Flow<EchoMessage> = packetsStream
        .mapNotNull {
            (it as? WSPacket.Echo)?.echoMessage
        }


    override val allEvents: Flow<WebsocketEvent> = packetsStream
        .mapNotNull {
            (it as? WSPacket.Event)?.event
        }

}