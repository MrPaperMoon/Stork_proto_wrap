package io.stork.client.module

import io.stork.proto.auth.*

interface Auth {
    suspend fun checkEmail(body:CheckEmailRequest): CheckEmailResponse
    suspend fun login(body:LoginRequest): LoginResponse
    suspend fun sendMagicLink(body:SendMagicLinkRequest): SendMagicLinkResponse
    suspend fun verifyMagicLinkCode(body:VerifyMagicLinkCodeRequest): LoginResponse
    suspend fun verifyMagicLink(body:VerifyMagicLinkRequest): LoginResponse
    suspend fun oauthGoogle(body:LoginWithGoogleRequest): LoginResponse
    suspend fun oauthSlack(body:LoginWithSlackRequest): LoginResponse
}