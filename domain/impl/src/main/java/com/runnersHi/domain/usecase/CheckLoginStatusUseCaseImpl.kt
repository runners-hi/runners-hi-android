package com.runnersHi.domain.usecase

import com.runnersHi.domain.repository.AuthRepository
import javax.inject.Inject

class CheckLoginStatusUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : CheckLoginStatusUseCase {

    override suspend fun invoke(): LoginCheckResult {
        val storedToken = authRepository.getStoredToken()
            ?: return LoginCheckResult.NotLoggedIn

        val result = authRepository.refreshToken(storedToken.refreshToken)

        return if (result.isSuccess) {
            LoginCheckResult.LoggedIn
        } else {
            LoginCheckResult.TokenRefreshFailed
        }
    }
}
