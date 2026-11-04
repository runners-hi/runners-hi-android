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
        authRepository.clearToken()
        userRepository.clearUser()
        regionRepository.clearSelectedRegion()
        termsRepository.clearAgreement()
    }
}
