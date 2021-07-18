package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.chat.message.*

interface ChatMessage {
    suspend fun get(body: GetChatMessagesRequest): ApiResult<GetChatMessagesResponse>
    suspend fun send(body: SendChatMessageRequest): ApiResult<SendChatMessageResponse>
    suspend fun edit(body: EditChatMessageRequest): ApiResult<EditChatMessageResponse>
    suspend fun toggleReaction(body: ToggleChatMessageReactionRequest): ApiResult<ToggleChatMessageReactionResponse>
}