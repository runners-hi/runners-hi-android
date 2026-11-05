package com.runnersHi.data.notification

import com.runnersHi.domain.notification.model.AppNotification
import com.runnersHi.domain.notification.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationRemoteDataSource
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<AppNotification>> =
        runCatching { remoteDataSource.fetchNotifications() }

    override suspend fun markAllRead(): Result<Unit> =
        runCatching { remoteDataSource.markAllRead() }

    override suspend fun isPushEnabled(): Result<Boolean> =
        runCatching { remoteDataSource.fetchPushEnabled() }

    override suspend fun setPushEnabled(enabled: Boolean): Result<Unit> =
        runCatching { remoteDataSource.updatePushEnabled(enabled) }
}
