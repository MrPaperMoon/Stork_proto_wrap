package io.stork.client.module

import io.stork.proto.member.MemberListRequest
import io.stork.proto.member.MemberListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Member {
    suspend fun list(body: MemberListRequest): MemberListResponse
}