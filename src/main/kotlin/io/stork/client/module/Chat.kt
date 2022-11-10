package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.messaging.chat.ArchiveChatRequest
import io.stork.proto.client.messaging.chat.ArchiveChatResponse
import io.stork.proto.client.messaging.chat.CreateChatRequest
import io.stork.proto.client.messaging.chat.CreateChatResponse
import io.stork.proto.client.messaging.chat.GetChatRequest
import io.stork.proto.client.messaging.chat.GetChatResponse
import io.stork.proto.client.messaging.chat.InviteToChatRequest
import io.stork.proto.client.messaging.chat.InviteToChatResponse
import io.stork.proto.client.messaging.chat.JoinChatRequest
import io.stork.proto.client.messaging.chat.JoinChatResponse
import io.stork.proto.client.messaging.chat.LeaveChatRequest
import io.stork.proto.client.messaging.chat.LeaveChatResponse
import io.stork.proto.client.messaging.chat.ListRecentChatsRequest
import io.stork.proto.client.messaging.chat.ListRecentChatsResponse
import io.stork.proto.client.messaging.chat.MarkChatAsReadRequest
import io.stork.proto.client.messaging.chat.MarkChatAsReadResponse
import io.stork.proto.client.messaging.chat.SearchChatRequest
import io.stork.proto.client.messaging.chat.SearchChatResponse
import io.stork.proto.client.messaging.chat.UpdateChatRequest
import io.stork.proto.client.messaging.chat.UpdateChatResponse

interface Chat {
    suspend fun get(body: GetChatRequest): ApiResult<GetChatResponse>
    suspend fun listRecentChats(body: ListRecentChatsRequest): ApiResult<ListRecentChatsResponse>

    suspend fun create(body: CreateChatRequest): ApiResult<CreateChatResponse>
    suspend fun update(body: UpdateChatRequest): ApiResult<UpdateChatResponse>
    suspend fun invite(body: InviteToChatRequest): ApiResult<InviteToChatResponse>
    suspend fun join(body: JoinChatRequest): ApiResult<JoinChatResponse>
    suspend fun leave(body: LeaveChatRequest): ApiResult<LeaveChatResponse>
    suspend fun archive(body: ArchiveChatRequest): ApiResult<ArchiveChatResponse>

    suspend fun markAsRead(body: MarkChatAsReadRequest): ApiResult<MarkChatAsReadResponse>
    suspend fun search(body: SearchChatRequest): ApiResult<SearchChatResponse>
}
