package io.stork.client.module

import io.stork.proto.account.*
import retrofit2.http.Body
import retrofit2.http.POST

interface Account {
    @POST("account.list")
    suspend fun list(@Body body:AccountsListRequest): AccountsListResponse

    @POST("account.updatePassword")
    suspend fun updatePassword(@Body body:UpdateAccountPasswordRequest): UpdateAccountPasswordResponse

    @POST("account.updateName")
    suspend fun updateName(@Body body:UpdateAccountNameRequest): UpdateAccountNameResponse
}