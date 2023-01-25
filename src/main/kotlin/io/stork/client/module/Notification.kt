package io.stork.client.module

import io.stork.client.ApiResult
import io.stork.proto.client.notifications.GetNotificationRequest
import io.stork.proto.client.notifications.GetNotificationResponse
import io.stork.proto.client.notifications.GetNotificationsListRequest
import io.stork.proto.client.notifications.GetNotificationsListResponse

interface Notification {
    suspend fun getNotification(request: GetNotificationRequest): ApiResult<GetNotificationResponse>
    suspend fun getNotificationsList(request: GetNotificationsListRequest): ApiResult<GetNotificationsListResponse>
}