package com.runnersHi.presentation.terms

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

class TermsAgreementContract {

    data class State(
        val isLoading: Boolean = true,
        val termsItems: List<TermsItemUiModel> = emptyList(),
        val allAgreed: Boolean = false,
        val canProceed: Boolean = false,
        val errorMessage: String? = null,
        val detailItem: TermsItemUiModel? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object LoadTerms : Event
        data object ToggleAllAgreed : Event
        data class ToggleTermItem(val id: String) : Event
        data object ProceedClicked : Event
        data class ViewTermsDetail(val termsItem: TermsItemUiModel) : Event
        data object DetailDismissed : Event
        data class DetailAgreed(val id: String) : Event
        data object ErrorDismissed : Event
        data object BackClicked : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToMain : Effect
        data object NavigateBack : Effect
        data class ShowToast(val message: String) : Effect
    }
}

/**
 * 약관 항목 UI 모델
 */
data class TermsItemUiModel(
    val id: String,
    val title: String,
    val content: String,
    val required: Boolean,
    val isAgreed: Boolean = false
)
