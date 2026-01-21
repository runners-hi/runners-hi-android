package com.runnersHi.presentation.splash

sealed interface SplashUiState {
    val progress: Float

    data class Loading(override val progress: Float = 0f) : SplashUiState
    data class NavigateToLogin(override val progress: Float = 1f) : SplashUiState
    data class NavigateToHome(override val progress: Float = 1f) : SplashUiState
    data class ForceUpdate(
        val currentVersion: String,
        val minVersion: String,
        override val progress: Float = 1f
    ) : SplashUiState
    data class Error(val message: String, override val progress: Float = 1f) : SplashUiState
}
