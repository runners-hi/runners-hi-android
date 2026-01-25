package com.runnersHi.data.terms

import com.runnersHi.domain.terms.model.TermsItem
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock 약관 DataSource
 * TODO: 실제 API 연동 시 RemoteTermsDataSource로 교체
 */
class MockTermsDataSource @Inject constructor() : TermsRemoteDataSource {

    override suspend fun getTermsList(): List<TermsItem> {
        // API 호출 시뮬레이션
        delay(500)

        return listOf(
            TermsItem(
                id = "service",
                title = "서비스 이용약관 동의",
                detailUrl = "https://example.com/terms/service",
                required = true
            ),
            TermsItem(
                id = "privacy",
                title = "개인정보 수집 및 이용 동의",
                detailUrl = "https://example.com/terms/privacy",
                required = true
            ),
            TermsItem(
                id = "marketing",
                title = "마케팅 정보 수신 동의",
                detailUrl = "https://example.com/terms/marketing",
                required = false
            ),
            TermsItem(
                id = "sns",
                title = "SNS 수신 동의",
                detailUrl = "https://example.com/terms/sns",
                required = false
            ),
            TermsItem(
                id = "age",
                title = "만 14세 이상입니다",
                detailUrl = "https://example.com/terms/age",
                required = true
            )
        )
    }

    override suspend fun agreeTerms(agreedTermIds: List<String>) {
        // API 호출 시뮬레이션
        delay(300)
        // 실제로는 서버에 동의 정보 전송
    }
}
