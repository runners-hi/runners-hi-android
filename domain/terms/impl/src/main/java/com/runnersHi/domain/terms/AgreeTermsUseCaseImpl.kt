package com.runnersHi.domain.terms

import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.terms.usecase.AgreeTermsUseCase
import javax.inject.Inject

class AgreeTermsUseCaseImpl @Inject constructor(
    private val termsRepository: TermsRepository
) : AgreeTermsUseCase {

    override suspend fun invoke(agreedTermIds: List<String>): Result<Unit> {
        return termsRepository.agreeTerms(agreedTermIds)
    }
}
