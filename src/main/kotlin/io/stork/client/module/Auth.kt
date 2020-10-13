package io.stork.client.module

import io.stork.proto.auth.*
import retrofit2.http.Body
import retrofit2.http.POST

interface Auth {
    @POST("auth.checkEmail")
    suspend fun checkEmail(@Body body:CheckEmailRequest): CheckEmailResponse
    @POST("auth.login")
    suspend fun login(@Body body:LoginRequest): LoginResponse
    @POST("auth.sendMagicLink")
    suspend fun sendMagicLink(@Body body:SendMagicLinkRequest): SendMagicLinkResponse
    @POST("auth.verifyMagicLinkCode")
    suspend fun verifyMagicLinkCode(@Body body:VerifyMagicLinkCodeRequest): LoginResponse
    @POST("auth.oauth.google")
    suspend fun oauthGoogle(@Body body:LoginWithGoogleRequest): LoginResponse
    @POST("auth.oauth.slack")
    suspend fun oauthSlack(@Body body:LoginWithSlackRequest): LoginResponse
}