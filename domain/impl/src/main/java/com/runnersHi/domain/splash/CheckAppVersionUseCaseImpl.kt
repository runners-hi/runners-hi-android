package com.runnersHi.domain.splash

import com.runnersHi.domain.splash.repository.AppConfigRepository
import com.runnersHi.domain.splash.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.splash.usecase.VersionCheckResult
import javax.inject.Inject

class CheckAppVersionUseCaseImpl @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : CheckAppVersionUseCase {

    override suspend fun invoke(currentVersion: String): VersionCheckResult {
        return try {
            val config = appConfigRepository.getAppConfig()

            when {
                config.maintenanceMode -> {
                    VersionCheckResult.Maintenance(config.maintenanceMessage)
                }
                isVersionLower(currentVersion, config.minVersion) -> {
                    VersionCheckResult.NeedsUpdate(config.minVersion, config.latestVersion)
                }
                else -> {
                    VersionCheckResult.UpToDate
                }
            }
        } catch (e: Exception) {
            VersionCheckResult.Error(e)
        }
    }

    private fun isVersionLower(current: String, min: String): Boolean {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val minParts = min.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(currentParts.size, minParts.size)

        for (i in 0 until maxLength) {
            val currentPart = currentParts.getOrElse(i) { 0 }
            val minPart = minParts.getOrElse(i) { 0 }

            if (currentPart < minPart) return true
            if (currentPart > minPart) return false
        }

        return false
    }
}
