package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.publicProfile.PublicProfileListRequest
import io.stork.proto.publicProfile.PublicProfileListResponse

interface PublicProfile {
    suspend fun list(body: PublicProfileListRequest): ApiResult<PublicProfileListResponse>
}