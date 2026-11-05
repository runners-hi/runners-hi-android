package com.runnersHi.presentation.mypage

import androidx.compose.runtime.Immutable
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object FeedbackContract {

    @Immutable
    data class State(
        val content: String = "",
        val isSending: Boolean = false
    ) : UiState {
        val canSend: Boolean get() = content.isNotBlank() && !isSending
    }

    sealed interface Event : UiEvent {
        data class ContentChanged(val content: String) : Event
        data object SendClicked : Event
    }

    sealed interface Effect : UiEffect {
        data class ShowToast(val message: String) : Effect
        data object Close : Effect
    }
}
