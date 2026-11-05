package com.runnersHi.domain.notification.usecase

import com.runnersHi.domain.notification.model.AppNotification

interface GetNotificationsUseCase {
    suspend operator fun invoke(): Result<List<AppNotification>>
}

interface MarkNotificationsReadUseCase {
    suspend operator fun invoke(): Result<Unit>
}

interface GetPushEnabledUseCase {
    suspend operator fun invoke(): Result<Boolean>
}

interface SetPushEnabledUseCase {
    suspend operator fun invoke(enabled: Boolean): Result<Unit>
}
