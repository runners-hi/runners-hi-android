package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.user.repository.UserRepository
import javax.inject.Inject

class LoginWithSocialUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val regionRepository: RegionRepository,
    private val termsRepository: TermsRepository
) : LoginWithSocialUseCase {

    override suspend fun invoke(
        socialLoginType: SocialLoginType,
        socialToken: String
    ): Result<LoginResult> {
        val result = authRepository.loginWithSocial(socialLoginType, socialToken)
        if (result.isSuccess) {
            // 프로필을 못 받아도 로그인은 유지한다 (마이페이지는 기본 이름으로 표시).
            runCatching { userRepository.refreshCurrentUser() }
            // 재가입이 아닌 사용자는 서버 기록으로 약관·지역 화면을 건너뛴다.
            runCatching { termsRepository.syncFromServer() }
            runCatching { regionRepository.syncFromServer() }
        }
        return result
    }
}
