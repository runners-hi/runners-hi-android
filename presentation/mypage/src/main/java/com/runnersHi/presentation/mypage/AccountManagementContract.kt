package com.runnersHi.presentation.mypage

import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object AccountManagementContract {

    data class State(
        val isLoggingOut: Boolean = false,
        val isWithdrawing: Boolean = false,
        val showWithdrawConfirm: Boolean = false
    ) : UiState

    sealed interface Event : UiEvent {
        data object LogoutClicked : Event
        data object WithdrawClicked : Event
        data object WithdrawConfirmed : Event
        data object WithdrawDismissed : Event
    }

    sealed interface Effect : UiEffect {
        data object LoggedOut : Effect
        data object Withdrawn : Effect
        data class ShowToast(val message: String) : Effect
    }
}
