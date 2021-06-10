package io.stork.client;

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.protobuf.ProtobufMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.stork.client.ktor.ProtobufFeature
import io.stork.client.ktor.StorkKtorResponseValidator
import io.stork.client.module.*
import io.stork.client.util.Signal
import io.stork.client.util.map
import io.stork.client.util.toPublisher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.seconds


interface ApiClient: SessionManager {
    val account: Account
    val auth: Auth
    val avatar: Avatar
    val chat: Chat
    val chatMessage: ChatMessage
    val conference: Conference
    val member: Member
    val publicProfile: PublicProfile
    val recordings: Recordings
    val rtc: RTC
    val session: Session
    val workspace: Workspace
    val websocket: EventWebsocket

    companion object {
        operator fun invoke(config: ApiClientConfig = ApiClientConfig()): ApiClient {
            val sessionManager: SessionManager = SessionManagerImpl()
            return ktorImpl(config, sessionManager)
        }

        private fun ktorImpl(config: ApiClientConfig, sessionManager: SessionManager): ApiClient {
            val client = createOkHttp(config, sessionManager)
            val ktorEngine = OkHttp.create {
                preconfigured = client
            }
            val websocket = webSocket(client, sessionManager, config.websocketUrl)
            val ktorClient = HttpClient(ktorEngine) {
                StorkKtorResponseValidator()
                when (config.mediaType) {
                    ApiMediaType.PROTOBUF -> install(ProtobufFeature)
                    ApiMediaType.JSON -> install(JsonFeature) {
                        serializer = JacksonSerializer(objectMapper)
                    }
                }
            }

            return KtorApiClient(config, ktorClient, sessionManager, websocket)
        }

        private fun createOkHttp(config: ApiClientConfig, sessionManager: SessionManager): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(sessionManager::sessionJwtToken))
                .addInterceptor(ContentTypeInterceptor(config.mediaType.contentType))
                .pingInterval(30, TimeUnit.SECONDS)
                .build()
        }

        private fun webSocket(client: OkHttpClient, sessionManager: SessionManager, websocketAddress: String): EventWebsocket {
            val lifecycle = LifecycleRegistry()

            val signal: Signal<String?> = sessionManager.sessionTokenChangedSignal
            signal.map { when (it) {
                null -> Lifecycle.State.Stopped.WithReason()
                else -> Lifecycle.State.Started
            }}
                .toPublisher()
                .subscribe(lifecycle)

            val scarlet = Scarlet.Builder()
                .webSocketFactory(client.newWebSocketFactory(websocketAddress))
                .lifecycle(lifecycle)
                .backoffStrategy(ExponentialBackoffStrategy(100, 30*1000))
                .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
                .addMessageAdapterFactory(ProtobufMessageAdapter.Factory())
                .build()
            return scarlet.create()

        }
    }

}