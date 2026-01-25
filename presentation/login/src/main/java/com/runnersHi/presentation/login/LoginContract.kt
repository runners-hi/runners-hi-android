package com.runnersHi.presentation.login

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

class LoginContract {

    data class State(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object KakaoLoginClicked : Event
        data object AppleLoginClicked : Event
        data class KakaoTokenReceived(val token: String) : Event
        data class AppleTokenReceived(val token: String) : Event
        data class LoginFailed(val message: String) : Event
        data object ErrorDismissed : Event
    }

    sealed interface Effect : UiEffect {
        data object RequestKakaoLogin : Effect
        data object RequestAppleLogin : Effect
        data object NavigateToHome : Effect
        data object NavigateToTermsAgreement : Effect  // 신규 유저 → 이용약관 화면
        data class ShowToast(val message: String) : Effect
    }
}
