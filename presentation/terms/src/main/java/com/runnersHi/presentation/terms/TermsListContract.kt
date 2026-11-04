package com.runnersHi.presentation.terms

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object TermsListContract {

    data class State(
        val isLoading: Boolean = true,
        val items: List<TermsItemUiModel> = emptyList(),
        val detailItem: TermsItemUiModel? = null,
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data class ItemClicked(val item: TermsItemUiModel) : Event
        data object DetailDismissed : Event
    }

    sealed interface Effect : UiEffect
}
