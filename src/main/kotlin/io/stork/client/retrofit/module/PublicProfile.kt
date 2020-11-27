package io.stork.client.retrofit.module

import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PublicProfile {
    @POST("publicProfile.list")
    fun list(@Body body:PublicProfileListRequest): Call<PublicProfileListResponse>
}