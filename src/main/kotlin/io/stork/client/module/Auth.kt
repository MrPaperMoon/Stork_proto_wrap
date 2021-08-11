package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.auth.auth.*

interface Auth {
    suspend fun checkEmail(body: CheckEmailRequest): ApiResult<CheckEmailResponse>
    suspend fun login(body: LoginRequest): ApiResult<LoginResponse>
    suspend fun sendMagicLink(body: SendMagicLinkRequest): ApiResult<SendMagicLinkResponse>
    suspend fun verifyMagicLinkCode(body:VerifyMagicLinkCodeRequest): ApiResult<LoginResponse>
    suspend fun verifyMagicLink(body:VerifyMagicLinkRequest): ApiResult<LoginResponse>
    suspend fun oauthGoogle(body:LoginWithGoogleRequest): ApiResult<LoginResponse>
    suspend fun oauthSlack(body:LoginWithSlackRequest): ApiResult<LoginResponse>
}