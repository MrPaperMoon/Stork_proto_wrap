package io.stork.client.retrofit.module

import io.stork.proto.calls.conference.*
import io.stork.proto.calls.rtc.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Conference {
    @POST("conference.create")
    fun create(@Body body:CreateConferenceRequest): Call<CreateConferenceResponse>

    @POST("conference.join")
    fun join(@Body body:JoinConferenceRequest): Call<JoinConferenceResponse>

    @POST("conference.list")
    fun list(@Body body:ConferenceListRequest): Call<ConferenceListResponse>

    @POST("conference.createConnection")
    fun createConnection(@Body body:CreateConferenceRTCConnectionRequest): Call<CreateConferenceRTCConnectionResponse>

    @POST("conference.closeConnection")
    fun closeConnection(@Body body:CloseConferenceRTCConnectionRequest): Call<CloseConferenceRTCConnectionResponse>

    @POST("conference.leave")
    fun leave(@Body body:LeaveConferenceRequest): Call<LeaveConferenceResponse>
}