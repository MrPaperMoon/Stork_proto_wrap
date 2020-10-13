package io.stork.client.module

import io.stork.proto.member.MemberListRequest
import io.stork.proto.member.MemberListResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface Member {
    @POST("member.list")
    suspend fun list(@Body body:MemberListRequest): MemberListResponse
}