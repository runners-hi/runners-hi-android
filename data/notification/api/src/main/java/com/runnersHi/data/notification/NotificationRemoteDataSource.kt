package com.runnersHi.data.notification

import com.runnersHi.domain.notification.model.AppNotification

interface NotificationRemoteDataSource {
    suspend fun fetchNotifications(): List<AppNotification>
    suspend fun markAllRead()
    suspend fun fetchPushEnabled(): Boolean
    suspend fun updatePushEnabled(enabled: Boolean)
}
