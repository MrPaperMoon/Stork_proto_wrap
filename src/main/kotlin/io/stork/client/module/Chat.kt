package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.chat.*

interface Chat {
    suspend fun get(body: GetChatRequest): ApiResult<GetChatResponse>
    suspend fun listRecentChats(body: ListRecentChatsRequest): ApiResult<ListRecentChatsResponse>

    suspend fun create(body: CreateChatRequest): ApiResult<CreateChatResponse>
    suspend fun update(body: UpdateChatRequest): ApiResult<UpdateChatResponse>
    suspend fun join(body: JoinChatRequest): ApiResult<JoinChatResponse>
    suspend fun leave(body: LeaveChatRequest): ApiResult<LeaveChatResponse>
    suspend fun archive(body: ArchiveChatRequest): ApiResult<ArchiveChatResponse>

    suspend fun markAsRead(body: MarkChatAsReadRequest): ApiResult<MarkChatAsReadResponse>
    suspend fun search(body: SearchChatRequest): ApiResult<SearchChatResponse>
}