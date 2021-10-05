package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.member.MemberListRequest
import io.stork.proto.client.member.MemberListResponse

interface Member {
    suspend fun list(body: MemberListRequest): ApiResult<MemberListResponse>
}
