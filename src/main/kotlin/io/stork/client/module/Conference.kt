package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.calls.conference.ConferenceInfoRequest
import io.stork.proto.client.calls.conference.ConferenceInfoResponse
import io.stork.proto.client.calls.conference.ConferenceListRequest
import io.stork.proto.client.calls.conference.ConferenceListResponse
import io.stork.proto.client.calls.conference.ConferenceVoiceChannelUpdateMuteRequest
import io.stork.proto.client.calls.conference.ConferenceVoiceChannelUpdateMuteResponse
import io.stork.proto.client.calls.conference.ConferenceWatercoolerUpdateScopeRequest
import io.stork.proto.client.calls.conference.ConferenceWatercoolerUpdateScopeResponse
import io.stork.proto.client.calls.conference.CreateConferenceRequest
import io.stork.proto.client.calls.conference.CreateConferenceResponse
import io.stork.proto.client.calls.conference.InviteToConferenceRequest
import io.stork.proto.client.calls.conference.InviteToConferenceResponse
import io.stork.proto.client.calls.conference.JoinConferenceRequest
import io.stork.proto.client.calls.conference.JoinConferenceResponse
import io.stork.proto.client.calls.conference.LeaveConferenceRequest
import io.stork.proto.client.calls.conference.LeaveConferenceResponse

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
