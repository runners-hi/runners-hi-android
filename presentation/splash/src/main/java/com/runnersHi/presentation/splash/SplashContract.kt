package com.runnersHi.presentation.splash

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

/**
 * Splash 화면의 MVI Contract
 * - 스플래시 로딩 + 로그인 UI를 하나의 화면에서 처리
 */
class SplashContract {

    /**
     * 화면 단계
     */
    enum class Phase {
        LOADING,      // 스플래시 로딩 중
        LOGIN,        // 로그인 UI 표시
        LOGGING_IN    // 로그인 진행 중
    }

    /**
     * Splash 화면 상태
     */
    data class State(
        val phase: Phase = Phase.LOADING,
        val progress: Float = 0f,
        val isLoading: Boolean = true,
        val forceUpdate: ForceUpdateInfo? = null,
        val errorMessage: String? = null
    ) : UiState

    data class ForceUpdateInfo(
        val currentVersion: String,
        val minVersion: String
    )

    /**
     * Splash 화면 이벤트 (사용자 액션)
     */
    sealed interface Event : UiEvent {
        // 스플래시 이벤트
        data class CheckAppStatus(val currentVersion: String) : Event
        data object RetryClicked : Event
        data object ForceUpdateConfirmed : Event

        // 로그인 이벤트
        data object KakaoLoginClicked : Event
        data object AppleLoginClicked : Event
        data class KakaoTokenReceived(val token: String) : Event
        data class AppleTokenReceived(val token: String) : Event
        data class LoginFailed(val message: String) : Event
    }

    /**
     * Splash 화면 일회성 이펙트
     */
    sealed interface Effect : UiEffect {
        // 스플래시 이펙트
        data object NavigateToHome : Effect
        data object OpenPlayStore : Effect
        data class ShowToast(val message: String) : Effect

        // 로그인 이펙트
        data object RequestKakaoLogin : Effect
        data object RequestAppleLogin : Effect
        data object NavigateToTermsAgreement : Effect
    }
}
