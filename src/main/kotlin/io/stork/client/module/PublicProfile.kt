package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.profile.publicProfile.PublicProfileListRequest
import io.stork.proto.client.profile.publicProfile.PublicProfileListResponse

interface PublicProfile {
    suspend fun list(body: PublicProfileListRequest): ApiResult<PublicProfileListResponse>
}