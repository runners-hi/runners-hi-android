package com.runnersHi.domain.terms.usecase

/**
 * 필수 약관 동의 여부 조회 UseCase
 */
interface HasAgreedTermsUseCase {
    suspend operator fun invoke(): Boolean
}
