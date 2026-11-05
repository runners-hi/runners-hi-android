package com.runnersHi.domain.notification

import com.runnersHi.domain.notification.model.AppNotification
import com.runnersHi.domain.notification.repository.NotificationRepository
import com.runnersHi.domain.notification.usecase.GetNotificationsUseCase
import com.runnersHi.domain.notification.usecase.GetPushEnabledUseCase
import com.runnersHi.domain.notification.usecase.MarkNotificationsReadUseCase
import com.runnersHi.domain.notification.usecase.SetPushEnabledUseCase
import javax.inject.Inject

class GetNotificationsUseCaseImpl @Inject constructor(
    private val repository: NotificationRepository
) : GetNotificationsUseCase {
    override suspend fun invoke(): Result<List<AppNotification>> = repository.getNotifications()
}

class MarkNotificationsReadUseCaseImpl @Inject constructor(
    private val repository: NotificationRepository
) : MarkNotificationsReadUseCase {
    override suspend fun invoke(): Result<Unit> = repository.markAllRead()
}

class GetPushEnabledUseCaseImpl @Inject constructor(
    private val repository: NotificationRepository
) : GetPushEnabledUseCase {
    override suspend fun invoke(): Result<Boolean> = repository.isPushEnabled()
}

class SetPushEnabledUseCaseImpl @Inject constructor(
    private val repository: NotificationRepository
) : SetPushEnabledUseCase {
    override suspend fun invoke(enabled: Boolean): Result<Unit> = repository.setPushEnabled(enabled)
}
