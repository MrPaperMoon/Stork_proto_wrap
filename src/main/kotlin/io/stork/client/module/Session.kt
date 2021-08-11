package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.session.session.*

interface Session {
    suspend fun generate(body: GenerateSessionRequest): ApiResult<GenerateSessionResponse>
    suspend fun updateClientSystemInfo(body: UpdateClientSystemInfoRequest): ApiResult<UpdateClientSystemInfoResponse>
    suspend fun updateTimezone(body: UpdateTimezoneRequest): ApiResult<UpdateTimezoneResponse>
    suspend fun logout(body:  LogoutRequest): ApiResult<LogoutResponse>
    suspend fun addPushNotificationsToken(body: AddPushNotificationsTokenRequest): ApiResult<AddPushNotificationsTokenResponse>
    suspend fun removePushNotificationsToken(body: RemovePushNotificationsTokenRequest): ApiResult<RemovePushNotificationsTokenResponse>
}