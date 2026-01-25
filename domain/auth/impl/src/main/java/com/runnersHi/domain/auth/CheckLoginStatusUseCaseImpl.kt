package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.LoginCheckResult
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
