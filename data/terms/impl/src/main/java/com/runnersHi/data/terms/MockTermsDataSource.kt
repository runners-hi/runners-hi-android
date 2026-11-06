package com.runnersHi.data.terms

import com.runnersHi.domain.terms.model.TermsItem
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock 약관 DataSource (문구는 iOS 앱과 동일)
 */
class MockTermsDataSource @Inject constructor() : TermsRemoteDataSource {

    override suspend fun getTermsList(): List<TermsItem> {
        // API 호출 시뮬레이션
        delay(500)
        // 서버(terms 테이블 display_order)와 같은 5개·같은 순서로 준다
        return MOCK_ORDER.mapNotNull { id -> BundledTerms.items.firstOrNull { it.id == id } }
    }

    override suspend fun agreeTerms(agreedTermIds: List<String>) {
        // API 호출 시뮬레이션
        delay(300)
    }
}

private val MOCK_ORDER = listOf("service", "privacy", "marketing", "sns", "age")
