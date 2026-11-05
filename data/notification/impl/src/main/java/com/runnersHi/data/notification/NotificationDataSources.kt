package com.runnersHi.data.notification

import com.runnersHi.core.network.NotificationSettingsRequest
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.core.network.apiCallNullable
import com.runnersHi.domain.notification.model.AppNotification
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

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

/** 서버 주소가 없을 때: 알림을 지어내지 않고 빈 목록을 준다. */
@Singleton
class EmptyNotificationDataSource @Inject constructor() : NotificationRemoteDataSource {
    private var pushEnabled = true

    override suspend fun fetchNotifications(): List<AppNotification> = emptyList()
    override suspend fun markAllRead() = Unit
    override suspend fun fetchPushEnabled(): Boolean = pushEnabled
    override suspend fun updatePushEnabled(enabled: Boolean) {
        pushEnabled = enabled
    }
}
