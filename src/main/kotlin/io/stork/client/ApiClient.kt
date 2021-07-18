package io.stork.client;

import io.stork.client.module.*


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