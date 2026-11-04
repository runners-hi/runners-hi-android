package com.runnersHi.presentation.mission

import androidx.compose.runtime.Immutable
import com.runnersHi.presentation.common.component.MissionItemModel
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object MissionContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val sections: List<MissionSectionUiModel> = emptyList(),
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
    }

    sealed interface Effect : UiEffect
}

@Immutable
data class MissionSectionUiModel(
    val id: String,
    val title: String,
    val bannerTitle: String?,
    val bannerPeriod: String?,
    val items: List<MissionItemModel>
)
