package io.stork.client;

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.protobuf.ProtobufMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import io.stork.client.module.*
import io.stork.client.util.Signal
import io.stork.client.util.map
import io.stork.client.util.toPublisher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.protobuf.ProtoConverterFactory
import kotlin.time.ExperimentalTime
import kotlin.time.seconds


interface ApiClient: SessionManager {
    val account: Account
    val auth: Auth
    val conference: Conference
    val member: Member
    val publicProfile: PublicProfile
    val rtc: RTC
    val session: Session
    val workspace: Workspace
    val websocket: EventWebsocket

    companion object {
        operator fun invoke(config: ApiClientConfig = ApiClientConfig()): ApiClient {
            val sessionManager: SessionManager = SessionManagerImpl()

            val client = OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(sessionManager::sessionJwtToken))
                    .addInterceptor(ContentTypeInterceptor(config.mediaType.contentType))
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = config.logLevel.impl
                    })
                    .build()

            val websocket = webSocket(client, sessionManager, config.websocketUrl)

            val retrofit = Retrofit.Builder()
                    .baseUrl(config.apiBaseUrl)
                    .client(client)
                    .addConverterFactory(config.mediaType.converterFactory)
                    .build()

            return RetrofitApiClient(retrofit, sessionManager, websocket)
        }

        @OptIn(ExperimentalTime::class)
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
                    .backoffStrategy(ExponentialBackoffStrategy(100, 30.seconds.toLongMilliseconds()))
                    .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
                    .addMessageAdapterFactory(ProtobufMessageAdapter.Factory())
                    .build()
            return scarlet.create()

        }
    }
}