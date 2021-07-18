package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.account.*

interface Account {
    suspend fun list(body: AccountsListRequest): ApiResult<AccountsListResponse>
    suspend fun updatePassword(body: UpdateAccountPasswordRequest): ApiResult<UpdateAccountPasswordResponse>
    suspend fun updateName(body: UpdateAccountNameRequest): ApiResult<UpdateAccountNameResponse>
}