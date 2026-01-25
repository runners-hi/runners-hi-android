package com.runnersHi.domain.terms.model

/**
 * 약관 항목 도메인 모델
 */
data class TermsItem(
    val id: String,
    val title: String,
    val detailUrl: String,
    val required: Boolean
)
