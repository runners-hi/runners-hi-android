package com.runnersHi.presentation.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object NavigateToLogin : SplashUiState
    data object NavigateToHome : SplashUiState
    data class ForceUpdate(
        val currentVersion: String,
        val minVersion: String
    ) : SplashUiState
    data class Error(val message: String) : SplashUiState
}
