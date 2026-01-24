package com.runnersHi.domain.auth.usecase

import com.runnersHi.domain.auth.model.SocialLoginType

interface LoginWithSocialUseCase {
    suspend operator fun invoke(
        socialLoginType: SocialLoginType,
        socialToken: String
    ): Result<LoginResult>
}

sealed class LoginResult {
    data object Success : LoginResult()
    data class NewUser(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
