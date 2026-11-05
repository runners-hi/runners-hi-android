package com.runnersHi.presentation.notification

import com.runnersHi.domain.notification.model.AppNotification
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object NotificationContract {

    data class State(
        val isLoading: Boolean = true,
        val notifications: List<AppNotification> = emptyList(),
        val pushEnabled: Boolean = true,
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data class PushToggled(val enabled: Boolean) : Event
    }

    sealed interface Effect : UiEffect {
        data class ShowToast(val message: String) : Effect
    }
}
