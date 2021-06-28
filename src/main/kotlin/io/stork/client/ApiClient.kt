package io.stork.client;

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import io.stork.client.ktor.ProtobufFeature
import io.stork.client.ktor.StorkKtorResponseValidator
import io.stork.client.ktor.WebSocketImpl
import io.stork.client.ktor.ws.KtorWebSocketProvider
import io.stork.client.ktor.ws.WebSocketSessionFactory
import io.stork.client.module.*
import io.stork.client.okhttp.AuthInterceptor
import io.stork.client.okhttp.ContentTypeInterceptor
import io.stork.client.okhttp.objectMapper
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


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
            return KtorApiClientFactory.create(config, sessionManager)
        }
    }
}