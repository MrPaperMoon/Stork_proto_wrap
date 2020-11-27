package io.stork.client.module

import io.stork.proto.calls.conference.*
import io.stork.proto.calls.rtc.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Conference {
    fun create(body: CreateConferenceRequest): CreateConferenceResponse

    fun join(body: JoinConferenceRequest): JoinConferenceResponse

    fun list(body: ConferenceListRequest): ConferenceListResponse

    fun createConnection(body: CreateConferenceRTCConnectionRequest): CreateConferenceRTCConnectionResponse

    fun closeConnection(body: CloseConferenceRTCConnectionRequest): CloseConferenceRTCConnectionResponse

    fun leave(body: LeaveConferenceRequest): LeaveConferenceResponse
}