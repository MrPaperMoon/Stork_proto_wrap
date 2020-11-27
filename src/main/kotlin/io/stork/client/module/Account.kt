package io.stork.client.module

import io.stork.proto.account.*

interface Account {
    suspend fun list(body: AccountsListRequest): AccountsListResponse
    suspend fun updatePassword(body: UpdateAccountPasswordRequest): UpdateAccountPasswordResponse
    suspend fun updateName(body: UpdateAccountNameRequest): UpdateAccountNameResponse
}