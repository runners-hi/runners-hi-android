package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import javax.inject.Inject

class LoginWithSocialUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LoginWithSocialUseCase {

    override suspend fun invoke(
        socialLoginType: SocialLoginType,
        socialToken: String
    ): Result<LoginResult> {
        return try {
            val result = authRepository.loginWithSocial(socialLoginType, socialToken)
            result.fold(
                onSuccess = { loginResult ->
                    Result.success(loginResult)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
