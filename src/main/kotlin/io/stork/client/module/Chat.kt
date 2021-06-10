package io.stork.client.module

import io.stork.proto.chat.*

interface Chat {
    suspend fun get(body: GetChatRequest): GetChatResponse
    suspend fun listRecentChats(body: ListRecentChatsRequest): ListRecentChatsResponse

    suspend fun create(body: CreateChatRequest): CreateChatResponse
    suspend fun update(body: UpdateChatRequest): UpdateChatResponse
    suspend fun join(body: JoinChatRequest): JoinChatResponse
    suspend fun leave(body: LeaveChatRequest): LeaveChatResponse
    suspend fun archive(body: ArchiveChatRequest): ArchiveChatResponse

    suspend fun markAsRead(body: MarkChatAsReadRequest): MarkChatAsReadResponse
    suspend fun search(body: SearchChatRequest): SearchChatResponse
}