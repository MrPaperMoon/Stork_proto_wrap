package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.auth.CheckEmailRequest
import io.stork.proto.client.auth.CheckEmailResponse
import io.stork.proto.client.auth.LoginRequest
import io.stork.proto.client.auth.LoginResponse
import io.stork.proto.client.auth.LoginWithGoogleRequest
import io.stork.proto.client.auth.LoginWithSlackRequest
import io.stork.proto.client.auth.SendMagicLinkRequest
import io.stork.proto.client.auth.SendMagicLinkResponse
import io.stork.proto.client.auth.VerifyMagicLinkCodeRequest
import io.stork.proto.client.auth.VerifyMagicLinkRequest

interface Auth {
    suspend fun checkEmail(body: CheckEmailRequest): ApiResult<CheckEmailResponse>
    suspend fun login(body: LoginRequest): ApiResult<LoginResponse>
    suspend fun sendMagicLink(body: SendMagicLinkRequest): ApiResult<SendMagicLinkResponse>
    suspend fun verifyMagicLinkCode(body: VerifyMagicLinkCodeRequest): ApiResult<LoginResponse>
    suspend fun verifyMagicLink(body: VerifyMagicLinkRequest): ApiResult<LoginResponse>
    suspend fun oauthGoogle(body: LoginWithGoogleRequest): ApiResult<LoginResponse>
    suspend fun oauthSlack(body: LoginWithSlackRequest): ApiResult<LoginResponse>
}
