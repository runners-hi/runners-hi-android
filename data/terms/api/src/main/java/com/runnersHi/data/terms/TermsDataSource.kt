package com.runnersHi.data.terms

import com.runnersHi.domain.terms.model.TermsItem

/**
 * 약관 Remote DataSource 인터페이스
 */
interface TermsRemoteDataSource {
    /**
     * 약관 목록 조회 (init API)
     */
    suspend fun getTermsList(): List<TermsItem>

    /**
     * 약관 동의 처리
     */
    suspend fun agreeTerms(agreedTermIds: List<String>)
}
