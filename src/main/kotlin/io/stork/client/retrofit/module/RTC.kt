package io.stork.client.retrofit.module

import io.stork.proto.calls.rtc.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RTC {
    @POST("rtc.createOffer")
    fun createOffer(@Body body:CreateRTCConnectionOfferRequest): Call<CreateRTCConnectionOfferResponse>

    @POST("rtc.addIceCandidates")
    fun addIceCandidates(@Body body:AddRTCIceCandidatesRequest): Call<AddRTCIceCandidatesResponse>

    @POST("rtc.removeIceCandidates")
    fun removeIceCandidates(@Body body:RemoveRTCIceCandidatesRequest): Call<RemoveRTCIceCandidatesResponse>

}