package com.runnersHi.presentation.launcher.api

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

/**
 * Launcher 화면의 MVI Contract
 * - Splash와 Login을 조합하여 하나의 플로우로 관리
 */
object LauncherContract {

    /**
     * 화면 단계
     */
    enum class Phase {
        SPLASH,       // 스플래시 로딩 중
        LOGIN,        // 로그인 UI 표시
        LOGGING_IN    // 로그인 진행 중
    }

    /**
     * Launcher 화면 상태
     */
    data class State(
        val phase: Phase = Phase.SPLASH,
        // Splash 상태
        val splashProgress: Float = 0f,
        val forceUpdateInfo: ForceUpdateInfo? = null,
        // Login 상태
        val isLoggingIn: Boolean = false,
        val loginError: String? = null
    ) : UiState

    data class ForceUpdateInfo(
        val currentVersion: String,
        val minVersion: String
    )

    /**
     * Launcher 화면 이벤트
     */
    sealed interface Event : UiEvent {
        // Splash 이벤트
        data class CheckAppStatus(val currentVersion: String) : Event
        data object ForceUpdateConfirmed : Event

        // Login 이벤트
        data object KakaoLoginClicked : Event
        data object AppleLoginClicked : Event
        data class KakaoTokenReceived(val token: String) : Event
        data class AppleTokenReceived(val token: String) : Event
        data class LoginFailed(val message: String) : Event
    }

    /**
     * Launcher 화면 일회성 이펙트
     */
    sealed interface Effect : UiEffect {
        // Splash 이펙트
        data object OpenPlayStore : Effect

        // Login 이펙트
        data object RequestKakaoLogin : Effect
        data object RequestAppleLogin : Effect

        // Navigation 이펙트
        data object NavigateToHome : Effect
        data object NavigateToTermsAgreement : Effect
    }
}
