package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.messaging.chat.EditChatMessageRequest
import io.stork.proto.client.messaging.chat.EditChatMessageResponse
import io.stork.proto.client.messaging.chat.GetChatMessagesRequest
import io.stork.proto.client.messaging.chat.GetChatMessagesResponse
import io.stork.proto.client.messaging.chat.RemoveChatMessageRequest
import io.stork.proto.client.messaging.chat.RemoveChatMessageResponse
import io.stork.proto.client.messaging.chat.SendChatMessageRequest
import io.stork.proto.client.messaging.chat.SendChatMessageResponse
import io.stork.proto.client.messaging.chat.ToggleChatMessageReactionRequest
import io.stork.proto.client.messaging.chat.ToggleChatMessageReactionResponse

interface ChatMessage {
    suspend fun get(body: GetChatMessagesRequest): ApiResult<GetChatMessagesResponse>
    suspend fun send(body: SendChatMessageRequest): ApiResult<SendChatMessageResponse>
    suspend fun edit(body: EditChatMessageRequest): ApiResult<EditChatMessageResponse>
    suspend fun toggleReaction(body: ToggleChatMessageReactionRequest): ApiResult<ToggleChatMessageReactionResponse>
    suspend fun remove(body: RemoveChatMessageRequest): ApiResult<RemoveChatMessageResponse>
}
