package com.runnersHi.presentation.terms

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.terms.usecase.GetTermsListUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermsListViewModel @Inject constructor(
    private val getTermsListUseCase: GetTermsListUseCase
) : MviViewModel<TermsListContract.State, TermsListContract.Event, TermsListContract.Effect>(
    TermsListContract.State()
) {

    init {
        load()
    }

    override fun handleEvent(event: TermsListContract.Event) {
        when (event) {
            TermsListContract.Event.Retry -> load()
            is TermsListContract.Event.ItemClicked -> updateState { copy(detailItem = event.item) }
            TermsListContract.Event.DetailDismissed -> updateState { copy(detailItem = null) }
        }
    }

    private fun load() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getTermsListUseCase()
                .onSuccess { terms ->
                    updateState {
                        copy(
                            isLoading = false,
                            items = terms.map {
                                TermsItemUiModel(it.id, it.title, it.content, it.required)
                            }
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "약관 목록을 불러오는데 실패했습니다."
                        )
                    }
                }
        }
    }
}
