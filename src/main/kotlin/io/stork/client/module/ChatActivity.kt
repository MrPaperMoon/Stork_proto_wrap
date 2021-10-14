package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.messaging.chat.StartChatActivityRequest
import io.stork.proto.client.messaging.chat.StartChatActivityResponse
import io.stork.proto.client.messaging.chat.StopChatActivityRequest
import io.stork.proto.client.messaging.chat.StopChatActivityResponse

interface ChatActivity {
    suspend fun start(body: StartChatActivityRequest): ApiResult<StartChatActivityResponse>
    suspend fun stop(body: StopChatActivityRequest): ApiResult<StopChatActivityResponse>
}
