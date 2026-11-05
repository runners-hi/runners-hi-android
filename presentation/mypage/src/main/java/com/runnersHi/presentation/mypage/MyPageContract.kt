package com.runnersHi.presentation.mypage

import androidx.compose.runtime.Immutable
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object MyPageContract {

    @Immutable
    data class State(
        val nickname: String = DEFAULT_NICKNAME,
        val profileImageUrl: String? = null,
        val regionName: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object RegionClicked : Event
        data object TermsClicked : Event
        data object AccountClicked : Event
        data object NoticesClicked : Event
        data object NotReadyClicked : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToRegion : Effect
        data object NavigateToTerms : Effect
        data object NavigateToAccount : Effect
        data object NavigateToNotices : Effect
        data class ShowToast(val message: String) : Effect
    }

    const val DEFAULT_NICKNAME = "러너"
}
