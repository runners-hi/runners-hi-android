package com.runnersHi.data.notification

import com.runnersHi.core.network.NotificationSettingsRequest
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.core.network.apiCallNullable
import com.runnersHi.domain.notification.model.AppNotification
import java.time.LocalDateTime
import javax.inject.Inject

class RemoteNotificationDataSource @Inject constructor(
    private val api: RunnersHiApi
) : NotificationRemoteDataSource {

    override suspend fun fetchNotifications(): List<AppNotification> =
        apiCall { api.getNotifications() }.map {
            AppNotification(
                id = it.id.toString(),
                title = it.title,
                body = it.body,
                isRead = it.isRead,
                createdAt = LocalDateTime.parse(it.createdAt)
            )
        }

    override suspend fun markAllRead() {
        apiCallNullable { api.markAllNotificationsRead() }
    }

    override suspend fun fetchPushEnabled(): Boolean = apiCall { api.getMyProfile() }.notificationEnabled

    override suspend fun updatePushEnabled(enabled: Boolean) {
        apiCallNullable { api.updateNotificationSettings(NotificationSettingsRequest(enabled)) }
    }
}
