package io.stork.client.ktor

import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.Message
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import io.stork.client.ApiClientConfig
import io.stork.client.LogLevel
import io.stork.client.SessionManager
import io.stork.client.ktor.ws.WSPacket
import io.stork.client.ktor.ws.WebSocketSession
import io.stork.client.ktor.ws.WebSocketSessionFactory
import io.stork.client.module.EventWebsocket
import io.stork.client.util.launchCatching
import io.stork.client.util.repeat
import io.stork.proto.calls.conference.ConferenceEvent
import io.stork.proto.calls.rtc.RTCEvent
import io.stork.proto.websocket.EchoMessage
import io.stork.proto.websocket.WebsocketEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import java.io.IOException

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

    private var wsConnectionParams: WsConnectionParameters? = null
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
        wsConnectionParams = WsConnectionParameters(address, sessionId)
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

    override val receiveEcho: Flow<EchoMessage> = session
        .filterNotNull()
        .flatMapConcat { it.parsedPackets }
        .mapNotNull {
            (it as? WSPacket.Echo)?.echoMessage
        }


    override val allEvents: Flow<WebsocketEvent> = session
        .filterNotNull()
        .flatMapConcat { it.parsedPackets }
        .mapNotNull {
            (it as? WSPacket.Event)?.event
        }

}