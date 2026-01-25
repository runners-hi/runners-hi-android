package com.runnersHi.presentation.terms

import androidx.lifecycle.viewModelScope
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermsAgreementViewModel @Inject constructor() : MviViewModel<TermsAgreementContract.State, TermsAgreementContract.Event, TermsAgreementContract.Effect>(
    initialState = TermsAgreementContract.State()
) {

    init {
        // 화면 진입 시 약관 목록 로드
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
        }
    }

    private fun loadTerms() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            // TODO: 실제 구현 시 UseCase를 통해 서버에서 약관 목록 가져오기
            delay(500) // 로딩 시뮬레이션

            val mockTerms = listOf(
                TermsItem(
                    id = "service",
                    title = "서비스 이용약관",
                    isRequired = true,
                    detailUrl = "https://example.com/terms/service"
                ),
                TermsItem(
                    id = "privacy",
                    title = "개인정보 처리방침",
                    isRequired = true,
                    detailUrl = "https://example.com/terms/privacy"
                ),
                TermsItem(
                    id = "location",
                    title = "위치정보 이용약관",
                    isRequired = true,
                    detailUrl = "https://example.com/terms/location"
                ),
                TermsItem(
                    id = "marketing",
                    title = "마케팅 정보 수신 동의",
                    isRequired = false,
                    detailUrl = "https://example.com/terms/marketing"
                )
            )

            updateState {
                copy(
                    isLoading = false,
                    termsItems = mockTerms,
                    canProceed = false
                )
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

    private fun checkCanProceed(items: List<TermsItem>): Boolean {
        // 필수 약관이 모두 동의되었는지 확인
        return items.filter { it.isRequired }.all { it.isAgreed }
    }

    private fun proceed() {
        if (!state.value.canProceed) {
            updateState { copy(errorMessage = "필수 약관에 모두 동의해주세요.") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            // TODO: 실제 구현 시 서버에 약관 동의 정보 전송
            delay(500) // API 호출 시뮬레이션

            updateState { copy(isLoading = false) }
            sendEffect(TermsAgreementContract.Effect.NavigateToMain)
        }
    }

    private fun viewTermsDetail(termsItem: TermsItem) {
        termsItem.detailUrl?.let { url ->
            sendEffect(TermsAgreementContract.Effect.OpenTermsWebView(url, termsItem.title))
        }
    }
}
