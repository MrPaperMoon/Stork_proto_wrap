package io.stork.client.module

import io.stork.proto.calls.rtc.*

interface RTC {
    suspend fun createOffer(body: CreateRTCConnectionOfferRequest): CreateRTCConnectionOfferResponse
    suspend fun addIceCandidates(body: AddRTCIceCandidatesRequest): AddRTCIceCandidatesResponse
    suspend fun removeIceCandidates(body: RemoveRTCIceCandidatesRequest): RemoveRTCIceCandidatesResponse
}