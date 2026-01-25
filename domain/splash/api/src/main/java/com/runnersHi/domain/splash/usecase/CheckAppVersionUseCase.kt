package com.runnersHi.domain.splash.usecase

interface CheckAppVersionUseCase {
    suspend operator fun invoke(currentVersion: String): VersionCheckResult
}

sealed interface VersionCheckResult {
    data object UpToDate : VersionCheckResult
    data class NeedsUpdate(val minVersion: String, val latestVersion: String) : VersionCheckResult
    data class Maintenance(val message: String) : VersionCheckResult
    data class Error(val exception: Throwable) : VersionCheckResult
}
