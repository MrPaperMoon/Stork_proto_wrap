package io.stork.client.module

import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PublicProfile {
    suspend fun list(body: PublicProfileListRequest): PublicProfileListResponse
}