package io.stork.client.module

import io.stork.proto.calls.conference.*

interface Conference {
    suspend fun create(body: CreateConferenceRequest): CreateConferenceResponse

    suspend fun join(body: JoinConferenceRequest): JoinConferenceResponse

    suspend fun list(body: ConferenceListRequest): ConferenceListResponse

    suspend fun leave(body: LeaveConferenceRequest): LeaveConferenceResponse

    suspend fun inviteToConference(body: InviteToConferenceRequest): InviteToConferenceResponse
    suspend fun watercoolerUpdateScope(body: ConferenceWatercoolerUpdateScopeRequest): ConferenceWatercoolerUpdateScopeResponse
    suspend fun conferenceInfo(body: ConferenceInfoRequest): ConferenceInfoResponse
    suspend fun conferenceVoiceChannelUpdateMute(body: ConferenceVoiceChannelUpdateMuteRequest): ConferenceVoiceChannelUpdateMuteResponse
}