package com.runnersHi.presentation.login.api

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

/**
 * Login 화면의 MVI Contract
 */
object LoginContract {

    /**
     * Login 화면 상태
     */
    data class State(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : UiState

    /**
     * Login 화면 이벤트 (사용자 액션)
     */
    sealed interface Event : UiEvent {
        data object KakaoLoginClicked : Event
        data object AppleLoginClicked : Event
        data class KakaoTokenReceived(val token: String) : Event
        data class AppleTokenReceived(val token: String) : Event
        data class LoginFailed(val message: String) : Event
        data object ErrorDismissed : Event
    }

    /**
     * Login 화면 일회성 이펙트 (내부 처리용)
     */
    sealed interface Effect : UiEffect {
        data object RequestKakaoLogin : Effect
        data object RequestAppleLogin : Effect
        data class ShowToast(val message: String) : Effect
    }

    /**
     * Login 결과 (Launcher로 전달)
     */
    sealed interface Result {
        data object NavigateToHome : Result
        data object NavigateToTermsAgreement : Result
    }
}
