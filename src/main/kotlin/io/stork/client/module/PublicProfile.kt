package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.profiles.PublicProfileListRequest
import io.stork.proto.client.profiles.PublicProfileListResponse

interface PublicProfile {
    suspend fun list(body: PublicProfileListRequest): ApiResult<PublicProfileListResponse>
}
