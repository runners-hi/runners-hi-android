package com.runnersHi.domain.auth.usecase

interface CheckLoginStatusUseCase {
    suspend operator fun invoke(): LoginCheckResult
}

sealed interface LoginCheckResult {
    data object NotLoggedIn : LoginCheckResult
    data object LoggedIn : LoginCheckResult
    data object TokenRefreshFailed : LoginCheckResult
}
