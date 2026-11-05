package com.runnersHi.domain.notification.repository

import com.runnersHi.domain.notification.model.AppNotification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<AppNotification>>
    suspend fun markAllRead(): Result<Unit>
    suspend fun isPushEnabled(): Result<Boolean>
    suspend fun setPushEnabled(enabled: Boolean): Result<Unit>
}
