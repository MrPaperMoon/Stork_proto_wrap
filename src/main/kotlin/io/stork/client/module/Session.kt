package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.session.AddPushNotificationsTokenRequest
import io.stork.proto.client.session.AddPushNotificationsTokenResponse
import io.stork.proto.client.session.GenerateSessionRequest
import io.stork.proto.client.session.GenerateSessionResponse
import io.stork.proto.client.session.LogoutRequest
import io.stork.proto.client.session.LogoutResponse
import io.stork.proto.client.session.RemovePushNotificationsTokenRequest
import io.stork.proto.client.session.RemovePushNotificationsTokenResponse
import io.stork.proto.client.session.UpdateClientSystemInfoRequest
import io.stork.proto.client.session.UpdateClientSystemInfoResponse
import io.stork.proto.client.session.UpdateTimezoneRequest
import io.stork.proto.client.session.UpdateTimezoneResponse

interface Session {
    suspend fun generate(body: GenerateSessionRequest): ApiResult<GenerateSessionResponse>
    suspend fun updateClientSystemInfo(body: UpdateClientSystemInfoRequest): ApiResult<UpdateClientSystemInfoResponse>
    suspend fun updateTimezone(body: UpdateTimezoneRequest): ApiResult<UpdateTimezoneResponse>
    suspend fun logout(body: LogoutRequest): ApiResult<LogoutResponse>
    suspend fun addPushNotificationsToken(body: AddPushNotificationsTokenRequest): ApiResult<AddPushNotificationsTokenResponse>
    suspend fun removePushNotificationsToken(body: RemovePushNotificationsTokenRequest): ApiResult<RemovePushNotificationsTokenResponse>
}
