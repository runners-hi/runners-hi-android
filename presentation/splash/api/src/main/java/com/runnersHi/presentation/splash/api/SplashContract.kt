package com.runnersHi.presentation.splash.api

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

/**
 * Splash 화면의 MVI Contract
 */
object SplashContract {

    /**
     * Splash 화면 상태
     */
    data class State(
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
     * Splash 화면 이벤트
     */
    sealed interface Event : UiEvent {
        data class CheckAppStatus(val currentVersion: String) : Event
        data object RetryClicked : Event
        data object ForceUpdateConfirmed : Event
    }

    /**
     * Splash 화면 이펙트
     */
    sealed interface Effect : UiEffect {
        data object OpenPlayStore : Effect
        data class ShowToast(val message: String) : Effect
    }

    /**
     * Splash 결과 (Launcher로 전달)
     */
    sealed interface Result {
        data object NavigateToLogin : Result
        data object NavigateToHome : Result
    }
}
