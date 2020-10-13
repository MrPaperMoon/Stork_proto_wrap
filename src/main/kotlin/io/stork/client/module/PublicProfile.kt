package io.stork.client.module

import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface PublicProfile {
    @POST("publicProfile.list")
    suspend fun list(@Body body:PublicProfileListRequest): PublicProfileListResponse
}