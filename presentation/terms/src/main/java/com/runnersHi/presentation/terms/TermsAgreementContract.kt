package com.runnersHi.presentation.terms

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

class TermsAgreementContract {

    data class State(
        val isLoading: Boolean = false,
        val termsItems: List<TermsItem> = emptyList(),
        val allAgreed: Boolean = false,
        val canProceed: Boolean = false,
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object LoadTerms : Event
        data object ToggleAllAgreed : Event
        data class ToggleTermItem(val id: String) : Event
        data object ProceedClicked : Event
        data class ViewTermsDetail(val termsItem: TermsItem) : Event
        data object ErrorDismissed : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToMain : Effect
        data class OpenTermsWebView(val url: String, val title: String) : Effect
        data class ShowToast(val message: String) : Effect
    }
}

/**
 * 약관 항목
 */
data class TermsItem(
    val id: String,
    val title: String,
    val isRequired: Boolean,
    val isAgreed: Boolean = false,
    val detailUrl: String? = null
)
