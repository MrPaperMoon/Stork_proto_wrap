package io.stork.client;

import io.stork.client.module.*
import io.stork.client.ws.WebSocketProvider


interface ApiClient: WebSocketProvider, SessionProvider {
    fun getConfig(): ApiClientConfig

    val account: Account
    val auth: Auth
    val avatar: Avatar
    val chat: Chat
    val chatActivity: ChatActivity
    val chatMessage: ChatMessage
    val conference: Conference
    val file: File
    val member: Member
    val publicProfile: PublicProfile
    val recordings: Recordings
    val rtc: RTC
    val session: Session
    val workspace: Workspace

    companion object {
        operator fun invoke(config: ApiClientConfig = ApiClientConfig(), sessionProvider: SessionProvider): ApiClient {
            return KtorApiClientFactory.create(config, sessionProvider)
        }
    }
}