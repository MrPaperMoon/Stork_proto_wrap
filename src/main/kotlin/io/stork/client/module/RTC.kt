package io.stork.client.module

import io.stork.proto.calls.rtc.*
import retrofit2.http.Body
import retrofit2.http.POST

interface RTC {
    @POST("rtc.createOffer")
    suspend fun createOffer(@Body body:CreateRTCConnectionOfferRequest): CreateRTCConnectionOfferResponse

    @POST("rtc.addIceCandidates")
    suspend fun addIceCandidates(@Body body:AddRTCIceCandidatesRequest): AddRTCIceCandidatesResponse

    @POST("rtc.removeIceCandidates")
    suspend fun removeIceCandidates(@Body body:RemoveRTCIceCandidatesRequest): RemoveRTCIceCandidatesResponse

}