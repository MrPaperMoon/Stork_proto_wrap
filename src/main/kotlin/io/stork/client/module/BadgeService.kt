package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.messaging.chat.GetBadgeRequest
import io.stork.proto.client.messaging.chat.GetBadgeResponse

interface BadgeService {
    suspend fun getBadge(request: GetBadgeRequest): ApiResult<GetBadgeResponse>
}