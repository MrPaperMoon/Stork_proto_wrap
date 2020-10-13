package io.stork.client.module

import io.stork.proto.calls.conference.*
import io.stork.proto.calls.rtc.*
import retrofit2.http.Body
import retrofit2.http.POST

interface Conference {
    @POST("conference.create")
    suspend fun create(@Body body:CreateConferenceRequest): CreateConferenceResponse

    @POST("conference.join")
    suspend fun join(@Body body:JoinConferenceRequest): JoinConferenceResponse

    @POST("conference.list")
    suspend fun list(@Body body:ConferenceListRequest): ConferenceListResponse

    @POST("conference.createConnection")
    suspend fun createConnection(@Body body:CreateConferenceRTCConnectionRequest): CreateConferenceRTCConnectionResponse

    @POST("conference.closeConnection")
    suspend fun closeConnection(@Body body:CloseConferenceRTCConnectionRequest): CloseConferenceRTCConnectionResponse

    @POST("conference.leave")
    suspend fun leave(@Body body:LeaveConferenceRequest): LeaveConferenceResponse

}