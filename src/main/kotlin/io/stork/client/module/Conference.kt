package io.stork.client.module

import io.stork.proto.calls.conference.*
import io.stork.proto.calls.rtc.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Conference {
    suspend fun create(body: CreateConferenceRequest): CreateConferenceResponse

    suspend fun join(body: JoinConferenceRequest): JoinConferenceResponse

    suspend fun list(body: ConferenceListRequest): ConferenceListResponse

    suspend fun createConnection(body: CreateConferenceRTCConnectionRequest): CreateConferenceRTCConnectionResponse

    suspend fun closeConnection(body: CloseConferenceRTCConnectionRequest): CloseConferenceRTCConnectionResponse

    suspend fun leave(body: LeaveConferenceRequest): LeaveConferenceResponse
}