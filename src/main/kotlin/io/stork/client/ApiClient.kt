package io.stork.client;

import io.stork.client.module.Account
import io.stork.client.module.Auth
import io.stork.client.module.Avatar
import io.stork.client.module.Chat
import io.stork.client.module.ChatActivity
import io.stork.client.module.ChatMessage
import io.stork.client.module.Conference
import io.stork.client.module.File
import io.stork.client.module.Member
import io.stork.client.module.PublicProfile
import io.stork.client.module.RTC
import io.stork.client.module.Recordings
import io.stork.client.module.Session
import io.stork.client.module.Workspace
import io.stork.client.ws.WebSocketProvider


interface ApiClient : WebSocketProvider, SessionProvider {
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
        operator fun invoke(config: ApiClientConfig = ApiClientConfig(),
                            sessionProvider: SessionProvider): ApiClient {
            return KtorApiClientFactory.create(config, sessionProvider)
        }
    }
}