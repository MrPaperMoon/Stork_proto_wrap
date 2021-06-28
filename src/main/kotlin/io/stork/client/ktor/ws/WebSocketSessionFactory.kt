package io.stork.client.ktor.ws

import io.ktor.utils.io.*
import io.stork.client.util.BackOffTimer
import io.stork.client.util.ExponentialBackOffTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import kotlin.time.ExperimentalTime

class WebSocketSessionFactory(
    private val webSocketProvider: WebSocketProvider,
) {
    private val sessionFactory = SessionFactory(webSocketProvider)
    private val log = LoggerFactory.getLogger(WebSocketSessionFactory::class.java)

    fun newReconnectingSession(address: String, sessionId: String, reconnectTimer: BackOffTimer = ExponentialBackOffTimer()): Flow<WebSocketSession> {
        return sessionObservable(address, sessionId, reconnectTimer)
    }

    @OptIn(ExperimentalTime::class)
    private fun sessionObservable(address: String, sessionId: String, reconnectTimer: BackOffTimer): Flow<WebSocketSession> {
        return flow {
            var lastSession: WebSocketSession? = null
            var lastSessionId = sessionId
            while (true) {
                try {
                    val newSession = createNewSession(address, lastSessionId)
                    emit(newSession)

                    lastSessionId = newSession.sessionId
                    lastSession = newSession
                    reconnectTimer.reset()
                    newSession.parsedPackets.collect()
                } catch (ex: CancellationException) {
                    log.debug("Coroutine cancelled, closing last session... quitting the session creation loop")
                    lastSession?.close()
                    break
                } catch (ex: Exception) {
                    lastSession = null
                    log.error("Unknown session error: ", ex)
                    val retryTimeout = reconnectTimer.nextTimeout()
                    log.debug("Will retry after timeout: {}", retryTimeout)
                    delay(retryTimeout.inWholeMilliseconds)
                }
                log.debug("KeksSession closed, creating a new session...")
            }
        }
    }

    private suspend fun createNewSession(address: String, sessionId: String): WebSocketSession = try {
        log.debug("Creating a new session for id {}", sessionId)
        sessionFactory.establishNewSession(address, sessionId).also {
            log.debug("New session created for id {}", sessionId)
        }
    } catch (ex: Exception) {
        log.debug("Failed to start new session, going to do error recovery. Reason: ", ex)
        errorRecovery(ex)
    }

    private suspend fun errorRecovery(failure: Throwable): WebSocketSession {
        // TODO: can we recover?
        throw failure
    }
}