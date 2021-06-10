package io.stork.client.module

import io.stork.proto.chat.message.*

interface ChatMessage {
    suspend fun get(body: GetChatMessagesRequest): GetChatMessagesResponse
    suspend fun send(body: SendChatMessageRequest): SendChatMessageResponse
    suspend fun edit(body: EditChatMessageRequest): EditChatMessageResponse
    suspend fun toggleReaction(body: ToggleChatMessageReactionRequest): ToggleChatMessageReactionResponse
}