package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.member.MemberListRequest
import io.stork.proto.member.MemberListResponse

interface Member {
    suspend fun list(body: MemberListRequest): ApiResult<MemberListResponse>
}