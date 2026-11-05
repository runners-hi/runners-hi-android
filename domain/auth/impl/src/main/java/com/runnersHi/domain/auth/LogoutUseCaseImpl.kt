package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.LogoutUseCase
import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.user.repository.UserRepository
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val regionRepository: RegionRepository,
    private val termsRepository: TermsRepository
) : LogoutUseCase {

    override suspend fun invoke() {
        // 서버 로그아웃이 실패해도 기기에서는 로그아웃한다.
        runCatching { authRepository.logoutFromServer() }
        authRepository.clearToken()
        userRepository.clearUser()
        regionRepository.clearSelectedRegion()
        termsRepository.clearAgreement()
    }
}
