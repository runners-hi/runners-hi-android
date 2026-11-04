package com.runnersHi.domain.health.usecase

import com.runnersHi.domain.health.model.HealthPermissionStatus

interface CheckHealthPermissionUseCase {
    suspend operator fun invoke(): HealthPermissionStatus
}
