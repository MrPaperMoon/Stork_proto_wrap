package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.calls.conference.*

interface Conference {
    suspend fun create(body: CreateConferenceRequest): ApiResult<CreateConferenceResponse>

    suspend fun join(body: JoinConferenceRequest): ApiResult<JoinConferenceResponse>

    suspend fun list(body: ConferenceListRequest): ApiResult<ConferenceListResponse>

    suspend fun leave(body: LeaveConferenceRequest): ApiResult<LeaveConferenceResponse>

    suspend fun inviteToConference(body: InviteToConferenceRequest): ApiResult<InviteToConferenceResponse>
    suspend fun watercoolerUpdateScope(body: ConferenceWatercoolerUpdateScopeRequest): ApiResult<ConferenceWatercoolerUpdateScopeResponse>
    suspend fun conferenceInfo(body: ConferenceInfoRequest): ApiResult<ConferenceInfoResponse>
    suspend fun conferenceVoiceChannelUpdateMute(body: ConferenceVoiceChannelUpdateMuteRequest): ApiResult<ConferenceVoiceChannelUpdateMuteResponse>
}
