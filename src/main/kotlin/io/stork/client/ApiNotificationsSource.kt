package io.stork.client

import io.stork.proto.client.notifications.Notification
import kotlinx.coroutines.flow.Flow

interface ApiNotificationsSource {
    val notifications: Flow<Notification>
}