package io.stork.client.module

import io.stork.proto.session.GenerateSessionRequest
import io.stork.proto.session.GenerateSessionResponse
import io.stork.proto.session.LogoutRequest
import io.stork.proto.session.LogoutResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface Session {
    @POST("session.generate")
    suspend fun generate(@Body body:GenerateSessionRequest): GenerateSessionResponse

    @POST("session.logout")
    suspend fun logout(@Body body: LogoutRequest): LogoutResponse
}