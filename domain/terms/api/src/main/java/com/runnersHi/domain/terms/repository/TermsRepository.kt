package com.runnersHi.domain.terms.repository

import com.runnersHi.domain.terms.model.TermsItem

/**
 * 약관 Repository 인터페이스
 */
interface TermsRepository {
    /**
     * 약관 목록 조회
     */
    suspend fun getTermsList(): Result<List<TermsItem>>

    /**
     * 약관 동의 처리
     * @param agreedTermIds 동의한 약관 ID 목록
     */
    suspend fun agreeTerms(agreedTermIds: List<String>): Result<Unit>
}
