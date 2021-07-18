package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.session.*

interface Session {
    suspend fun generate(body: GenerateSessionRequest): ApiResult<GenerateSessionResponse>
    suspend fun logout(body:  LogoutRequest): ApiResult<LogoutResponse>
    suspend fun addPushNotificationsToken(body: AddPushNotificationsTokenRequest): ApiResult<AddPushNotificationsTokenResponse>
    suspend fun removePushNotificationsToken(body: RemovePushNotificationsTokenRequest): ApiResult<RemovePushNotificationsTokenResponse>
}