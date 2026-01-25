package com.runnersHi.domain.terms.usecase

import com.runnersHi.domain.terms.model.TermsItem

/**
 * 약관 목록 조회 UseCase
 */
interface GetTermsListUseCase {
    suspend operator fun invoke(): Result<List<TermsItem>>
}
