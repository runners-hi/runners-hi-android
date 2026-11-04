package com.runnersHi.domain.terms

import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.terms.usecase.HasAgreedTermsUseCase
import javax.inject.Inject

class HasAgreedTermsUseCaseImpl @Inject constructor(
    private val termsRepository: TermsRepository
) : HasAgreedTermsUseCase {

    override suspend fun invoke(): Boolean = termsRepository.hasAgreedTerms()
}
