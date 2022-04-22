package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.calls.rtc.AddRTCIceCandidatesRequest
import io.stork.proto.client.calls.rtc.AddRTCIceCandidatesResponse
import io.stork.proto.client.calls.rtc.RTCConnectionNegotiateRequest
import io.stork.proto.client.calls.rtc.RTCConnectionNegotiateResponse
import io.stork.proto.client.calls.rtc.RemoveRTCIceCandidatesRequest
import io.stork.proto.client.calls.rtc.RemoveRTCIceCandidatesResponse

interface RTC {
    suspend fun negotiateConnection(body: RTCConnectionNegotiateRequest): ApiResult<RTCConnectionNegotiateResponse>
    suspend fun addIceCandidates(body: AddRTCIceCandidatesRequest): ApiResult<AddRTCIceCandidatesResponse>
    suspend fun removeIceCandidates(body: RemoveRTCIceCandidatesRequest): ApiResult<RemoveRTCIceCandidatesResponse>
}
