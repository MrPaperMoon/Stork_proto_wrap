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
import io.ktor.client.features.websocket.*
import io.stork.client.ktor.KtorWebSocket
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
    val file: File
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
            val ktorClient = HttpClient(ktorEngine) {
                install(WebSockets) {
                    pingInterval = 30_000
                }
                StorkKtorResponseValidator()
                when (config.mediaType) {
                    ApiMediaType.PROTOBUF -> install(ProtobufFeature)
                    ApiMediaType.JSON -> install(JsonFeature) {
                        serializer = JacksonSerializer(objectMapper)
                    }
                }
            }
            val websocket = KtorWebSocket(config, ktorClient)
            return KtorApiClient(config, ktorClient, sessionManager, websocket)
        }

        private fun createOkHttp(config: ApiClientConfig, sessionManager: SessionManager): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(sessionManager::sessionJwtToken))
                .addInterceptor(ContentTypeInterceptor(config.mediaType.contentType))
                .pingInterval(30, TimeUnit.SECONDS)
                .build()
        }
    }
}