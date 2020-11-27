package io.stork.client.module

import io.stork.proto.session.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Session {
    suspend fun generate(body: GenerateSessionRequest): GenerateSessionResponse
    suspend fun logout(body:  LogoutRequest): LogoutResponse
    suspend fun addPushNotificationsToken(body: AddPushNotificationsTokenRequest): AddPushNotificationsTokenResponse
    suspend fun removePushNotificationsToken(body: RemovePushNotificationsTokenRequest): RemovePushNotificationsTokenResponse
}