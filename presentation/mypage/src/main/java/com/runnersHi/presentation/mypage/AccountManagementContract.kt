package com.runnersHi.presentation.mypage

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object AccountManagementContract {

    data class State(val isLoggingOut: Boolean = false) : UiState

    sealed interface Event : UiEvent {
        data object LogoutClicked : Event
    }

    sealed interface Effect : UiEffect {
        data object LoggedOut : Effect
    }
}
