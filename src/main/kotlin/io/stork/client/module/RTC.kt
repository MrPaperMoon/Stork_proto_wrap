package io.stork.client.module

import io.stork.proto.calls.rtc.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RTC {
    suspend fun createOffer(body: CreateRTCConnectionOfferRequest): CreateRTCConnectionOfferResponse
    suspend fun addIceCandidates(body: AddRTCIceCandidatesRequest): AddRTCIceCandidatesResponse
    suspend fun removeIceCandidates(body: RemoveRTCIceCandidatesRequest): RemoveRTCIceCandidatesResponse
}