package io.stork.client.module

import io.stork.proto.calls.rtc.*

interface RTC {
    suspend fun negotiateConnection(body: RTCConnectionNegotiateRequest): RTCConnectionNegotiateResponse
    suspend fun addIceCandidates(body: AddRTCIceCandidatesRequest): AddRTCIceCandidatesResponse
    suspend fun removeIceCandidates(body: RemoveRTCIceCandidatesRequest): RemoveRTCIceCandidatesResponse
}