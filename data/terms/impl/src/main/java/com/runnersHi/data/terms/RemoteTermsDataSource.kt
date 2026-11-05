package com.runnersHi.data.terms

import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.TermsAgreementItem
import com.runnersHi.core.network.TermsAgreementRequest
import com.runnersHi.core.network.TermsResponse
import com.runnersHi.core.network.apiCall
import com.runnersHi.core.network.apiCallNullable
import com.runnersHi.domain.terms.model.TermsItem
import javax.inject.Inject

class RemoteTermsDataSource @Inject constructor(
    private val api: RunnersHiApi
) : TermsRemoteDataSource {

    override suspend fun getTermsList(): List<TermsItem> =
        apiCall { api.getTerms() }.map { it.toItem() }

    override suspend fun agreeTerms(agreedTermIds: List<String>) {
        val request = TermsAgreementRequest(
            agreedTermIds.map { TermsAgreementItem(it.toLong(), agreed = true) }
        )
        apiCallNullable { api.agreeTerms(request) }
    }

    override suspend fun fetchHasAgreedRequired(): Boolean =
        apiCall { api.getTerms() }.filter { it.required }.all { it.agreed }
}

internal fun TermsResponse.toItem() = TermsItem(
    id = id.toString(),
    title = title,
    content = BundledTerms.items.firstOrNull { it.id == BUNDLED_ID[code] }?.content.orEmpty(),
    required = required
)

private val BUNDLED_ID = mapOf(
    "TERMS_OF_SERVICE" to "service",
    "PRIVACY_POLICY" to "privacy",
    "MARKETING" to "marketing",
    "SNS" to "sns",
    "AGE_VERIFICATION" to "age"
)
