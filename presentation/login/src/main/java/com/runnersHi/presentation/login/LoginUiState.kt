package com.runnersHi.presentation.login

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class NewUser(val userId: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}
