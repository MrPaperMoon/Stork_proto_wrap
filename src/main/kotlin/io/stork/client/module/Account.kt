package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.account.AccountsListRequest
import io.stork.proto.client.account.AccountsListResponse
import io.stork.proto.client.account.UpdateAccountNameRequest
import io.stork.proto.client.account.UpdateAccountNameResponse
import io.stork.proto.client.account.UpdateAccountPasswordRequest
import io.stork.proto.client.account.UpdateAccountPasswordResponse

interface Account {
    suspend fun list(body: AccountsListRequest): ApiResult<AccountsListResponse>
    suspend fun updatePassword(body: UpdateAccountPasswordRequest): ApiResult<UpdateAccountPasswordResponse>
    suspend fun updateName(body: UpdateAccountNameRequest): ApiResult<UpdateAccountNameResponse>
}
