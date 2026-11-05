package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.WithdrawUseCase
import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.user.repository.UserRepository
import javax.inject.Inject

class WithdrawUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val regionRepository: RegionRepository,
    private val termsRepository: TermsRepository
) : WithdrawUseCase {

    override suspend fun invoke(): Result<Unit> = authRepository.withdraw().onSuccess {
        authRepository.clearToken()
        userRepository.clearUser()
        regionRepository.clearSelectedRegion()
        termsRepository.clearAgreement()
    }
}
