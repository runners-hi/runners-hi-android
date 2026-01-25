package com.runnersHi.domain.terms.usecase

/**
 * 약관 동의 UseCase
 */
interface AgreeTermsUseCase {
    suspend operator fun invoke(agreedTermIds: List<String>): Result<Unit>
}
