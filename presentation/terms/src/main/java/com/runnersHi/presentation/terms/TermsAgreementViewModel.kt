package com.runnersHi.presentation.terms

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.terms.usecase.AgreeTermsUseCase
import com.runnersHi.domain.terms.usecase.GetTermsListUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermsAgreementViewModel @Inject constructor(
    private val getTermsListUseCase: GetTermsListUseCase,
    private val agreeTermsUseCase: AgreeTermsUseCase
) : MviViewModel<TermsAgreementContract.State, TermsAgreementContract.Event, TermsAgreementContract.Effect>(
    initialState = TermsAgreementContract.State()
) {

    init {
        sendEvent(TermsAgreementContract.Event.LoadTerms)
    }

    override fun handleEvent(event: TermsAgreementContract.Event) {
        when (event) {
            is TermsAgreementContract.Event.LoadTerms -> loadTerms()
            is TermsAgreementContract.Event.ToggleAllAgreed -> toggleAllAgreed()
            is TermsAgreementContract.Event.ToggleTermItem -> toggleTermItem(event.id)
            is TermsAgreementContract.Event.ProceedClicked -> proceed()
            is TermsAgreementContract.Event.ViewTermsDetail -> viewTermsDetail(event.termsItem)
            is TermsAgreementContract.Event.ErrorDismissed -> {
                updateState { copy(errorMessage = null) }
            }
            is TermsAgreementContract.Event.BackClicked -> {
                sendEffect(TermsAgreementContract.Effect.NavigateBack)
            }
        }
    }

    private fun loadTerms() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            getTermsListUseCase()
                .onSuccess { termsList ->
                    val uiModels = termsList.map { item ->
                        TermsItemUiModel(
                            id = item.id,
                            title = item.title,
                            detailUrl = item.detailUrl,
                            required = item.required,
                            isAgreed = false
                        )
                    }
                    updateState {
                        copy(
                            isLoading = false,
                            termsItems = uiModels,
                            canProceed = false
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

    private fun toggleAllAgreed() {
        val currentAllAgreed = state.value.allAgreed
        val newAllAgreed = !currentAllAgreed

        val updatedItems = state.value.termsItems.map {
            it.copy(isAgreed = newAllAgreed)
        }

        updateState {
            copy(
                termsItems = updatedItems,
                allAgreed = newAllAgreed,
                canProceed = checkCanProceed(updatedItems)
            )
        }
    }

    private fun toggleTermItem(id: String) {
        val updatedItems = state.value.termsItems.map {
            if (it.id == id) it.copy(isAgreed = !it.isAgreed) else it
        }

        val allAgreed = updatedItems.all { it.isAgreed }

        updateState {
            copy(
                termsItems = updatedItems,
                allAgreed = allAgreed,
                canProceed = checkCanProceed(updatedItems)
            )
        }
    }

    private fun checkCanProceed(items: List<TermsItemUiModel>): Boolean {
        return items.filter { it.required }.all { it.isAgreed }
    }

    private fun proceed() {
        if (!state.value.canProceed) {
            updateState { copy(errorMessage = "필수 약관에 모두 동의해주세요.") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            val agreedTermIds = state.value.termsItems
                .filter { it.isAgreed }
                .map { it.id }

            agreeTermsUseCase(agreedTermIds)
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    sendEffect(TermsAgreementContract.Effect.NavigateToMain)
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "약관 동의 처리에 실패했습니다."
                        )
                    }
                }
        }
    }

    private fun viewTermsDetail(termsItem: TermsItemUiModel) {
        sendEffect(TermsAgreementContract.Effect.OpenTermsWebView(termsItem.detailUrl, termsItem.title))
    }
}
