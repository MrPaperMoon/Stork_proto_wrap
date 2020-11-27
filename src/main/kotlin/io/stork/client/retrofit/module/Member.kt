package io.stork.client.retrofit.module

import io.stork.proto.member.MemberListRequest
import io.stork.proto.member.MemberListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Member {
    @POST("member.list")
    fun list(@Body body:MemberListRequest): Call<MemberListResponse>
}