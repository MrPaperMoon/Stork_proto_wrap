package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.calls.rtc.*

interface RTC {
    suspend fun negotiateConnection(body: RTCConnectionNegotiateRequest): ApiResult<RTCConnectionNegotiateResponse>
    suspend fun addIceCandidates(body: AddRTCIceCandidatesRequest): ApiResult<AddRTCIceCandidatesResponse>
    suspend fun removeIceCandidates(body: RemoveRTCIceCandidatesRequest): ApiResult<RemoveRTCIceCandidatesResponse>
}