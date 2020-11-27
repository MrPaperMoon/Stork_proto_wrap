package io.stork.client.retrofit.module

import io.stork.proto.auth.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Auth {
    @POST("auth.checkEmail")
    fun checkEmail(@Body body:CheckEmailRequest): Call<CheckEmailResponse>
    @POST("auth.login")
    fun login(@Body body:LoginRequest): Call<LoginResponse>
    @POST("auth.sendMagicLink")
    fun sendMagicLink(@Body body:SendMagicLinkRequest): Call<SendMagicLinkResponse>
    @POST("auth.verifyMagicLinkCode")
    fun verifyMagicLinkCode(@Body body:VerifyMagicLinkCodeRequest): Call<LoginResponse>
    @POST("auth.verifyMagicLink")
    fun verifyMagicLink(@Body body:VerifyMagicLinkRequest): Call<LoginResponse>
    @POST("auth.oauth.google")
    fun oauthGoogle(@Body body:LoginWithGoogleRequest): Call<LoginResponse>
    @POST("auth.oauth.slack")
    fun oauthSlack(@Body body:LoginWithSlackRequest): Call<LoginResponse>
}