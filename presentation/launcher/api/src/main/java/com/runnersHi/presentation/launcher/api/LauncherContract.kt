package com.runnersHi.presentation.launcher.api

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState
import com.runnersHi.presentation.login.api.LoginContract
import com.runnersHi.presentation.splash.api.SplashContract

/**
 * Launcher 화면의 MVI Contract
 * - Splash와 Login을 조합하여 하나의 플로우로 관리
 * - splash-api, login-api의 타입을 재사용
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
     * - splash-api, login-api의 State를 조합
     */
    data class State(
        val phase: Phase = Phase.SPLASH,
        val splashState: SplashContract.State = SplashContract.State(),
        val loginState: LoginContract.State = LoginContract.State()
    ) : UiState

    /**
     * Launcher 화면 이벤트
     * - splash-api, login-api의 Event를 래핑
     */
    sealed interface Event : UiEvent {
        // Splash 이벤트
        data class CheckAppStatus(val currentVersion: String) : Event
        data object ForceUpdateConfirmed : Event

        // Login 이벤트 (LoginContract.Event를 그대로 전달)
        data class LoginEvent(val event: LoginContract.Event) : Event
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
