package com.runnersHi.presentation.login.api

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

/**
 * Login 화면의 MVI Contract
 */
object LoginContract {

    /**
     * 로그인 로딩 타입
     */
    enum class LoadingType {
        NONE,
        KAKAO,
        APPLE,
        REVIEW,
        DEV
    }

    /**
     * Login 화면 상태
     */
    data class State(
        val loadingType: LoadingType = LoadingType.NONE,
        val errorMessage: String? = null,
        val showAppleLogin: Boolean = true
    ) : UiState {
        val isLoading: Boolean get() = loadingType != LoadingType.NONE
        val isKakaoLoading: Boolean get() = loadingType == LoadingType.KAKAO
        val isAppleLoading: Boolean get() = loadingType == LoadingType.APPLE
    }

    /**
     * Login 화면 이벤트
     */
    sealed interface Event : UiEvent {
        data object KakaoLoginClicked : Event
        data object AppleLoginClicked : Event
        data class KakaoTokenReceived(val token: String) : Event
        data class AppleTokenReceived(val token: String) : Event
        /** 로고를 7번 눌러 입력한 스토어 심사 코드 */
        data class ReviewCodeSubmitted(val code: String) : Event
        /** 개발 서버 전용 로그인(userKey만). */
        data class DevLoginSubmitted(val userKey: String) : Event
        data class LoginFailed(val message: String) : Event
        data object LoginCancelled : Event
        data object ErrorDismissed : Event
    }

    /**
     * Login 화면 일회성 이펙트
     */
    sealed interface Effect : UiEffect {
        data object RequestKakaoLogin : Effect
        data object RequestAppleLogin : Effect
        data object NavigateToHome : Effect
        data object NavigateToTermsAgreement : Effect
        data class ShowToast(val message: String) : Effect
    }
}
