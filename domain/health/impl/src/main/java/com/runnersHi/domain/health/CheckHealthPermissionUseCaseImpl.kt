package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.HealthPermissionStatus
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.usecase.CheckHealthPermissionUseCase
import javax.inject.Inject

class CheckHealthPermissionUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository
) : CheckHealthPermissionUseCase {
    override suspend fun invoke(): HealthPermissionStatus {
        return healthRepository.checkPermissionStatus()
    }
}
