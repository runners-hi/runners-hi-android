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
        return authRepository.loginWithSocial(socialLoginType, socialToken)
    }
}
