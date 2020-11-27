package io.stork.client.retrofit.module

import io.stork.proto.session.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Session {
    @POST("session.generate")
    fun generate(@Body body:GenerateSessionRequest): Call<GenerateSessionResponse>

    @POST("session.logout")
    fun logout(@Body body: LogoutRequest): Call<LogoutResponse>

    @POST("session.addPushNotificationsToken")
    fun addPushNotificationsToken(@Body body:AddPushNotificationsTokenRequest): Call<AddPushNotificationsTokenResponse>

    @POST("session.removePushNotificationsToken")
    fun removePushNotificationsToken(@Body body:RemovePushNotificationsTokenRequest): Call<RemovePushNotificationsTokenResponse>
}