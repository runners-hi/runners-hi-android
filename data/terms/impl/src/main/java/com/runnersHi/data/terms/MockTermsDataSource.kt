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
        return BundledTerms.items
    }

    override suspend fun agreeTerms(agreedTermIds: List<String>) {
        // API 호출 시뮬레이션
        delay(300)
    }
}
