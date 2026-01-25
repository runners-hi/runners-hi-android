package com.runnersHi.domain.terms

import com.runnersHi.domain.terms.model.TermsItem
import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.terms.usecase.GetTermsListUseCase
import javax.inject.Inject

class GetTermsListUseCaseImpl @Inject constructor(
    private val termsRepository: TermsRepository
) : GetTermsListUseCase {

    override suspend fun invoke(): Result<List<TermsItem>> {
        return termsRepository.getTermsList()
    }
}
