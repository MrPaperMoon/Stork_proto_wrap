package io.stork.client

import io.stork.client.module.*
import io.stork.client.retrofit.RetrofitAdapter.createAdapted
import retrofit2.Retrofit

internal class RetrofitApiClient(
        retrofit: Retrofit,
        private val sessionManager: SessionManager,
        override val websocket: EventWebsocket
): ApiClient, SessionManager by sessionManager {
    override val account: Account = retrofit.createAdapted(io.stork.client.retrofit.module.Account::class)
    override val auth: Auth = retrofit.createAdapted(io.stork.client.retrofit.module.Auth::class)
    override val conference: Conference = retrofit.createAdapted(io.stork.client.retrofit.module.Conference::class)
    override val member: Member = retrofit.createAdapted(io.stork.client.retrofit.module.Member::class)
    override val publicProfile: PublicProfile = retrofit.createAdapted(io.stork.client.retrofit.module.PublicProfile::class)
    override val rtc: RTC = retrofit.createAdapted(io.stork.client.retrofit.module.RTC::class)
    override val session: Session = retrofit.createAdapted(io.stork.client.retrofit.module.Session::class)
    override val workspace: Workspace = retrofit.createAdapted(io.stork.client.retrofit.module.Workspace::class)
}