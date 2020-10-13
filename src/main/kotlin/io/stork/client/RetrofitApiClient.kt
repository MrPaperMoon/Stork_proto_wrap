package io.stork.client

import io.stork.client.module.*
import retrofit2.Retrofit
import retrofit2.create

internal class RetrofitApiClient(
        retrofit: Retrofit,
        private val sessionManager: SessionManager,
        override val websocket: EventWebsocket
): ApiClient, SessionManager by sessionManager {
    override val account: Account = retrofit.create()
    override val auth: Auth = retrofit.create()
    override val conference: Conference = retrofit.create()
    override val member: Member = retrofit.create()
    override val publicProfile: PublicProfile = retrofit.create()
    override val rtc: RTC = retrofit.create()
    override val session: Session = retrofit.create()
    override val workspace: Workspace = retrofit.create()
}