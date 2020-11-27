package io.stork.client.retrofit.module

import io.stork.proto.account.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Account {
    @POST("account.list")
    fun list(@Body body:AccountsListRequest): Call<AccountsListResponse>

    @POST("account.updatePassword")
    fun updatePassword(@Body body:UpdateAccountPasswordRequest): Call<UpdateAccountPasswordResponse>

    @POST("account.updateName")
    fun updateName(@Body body:UpdateAccountNameRequest): Call<UpdateAccountNameResponse>
}