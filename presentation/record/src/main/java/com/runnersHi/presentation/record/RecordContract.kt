package com.runnersHi.presentation.record

import androidx.compose.runtime.Immutable
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object RecordContract {

    enum class Access { READY, NEEDS_INSTALL, NEEDS_PERMISSION }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val access: Access = Access.READY,
        val today: TodayRecordUiModel? = null,
        val past: List<PastRecordUiModel> = emptyList(),
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object Refresh : Event
        data object GrantPermissionClicked : Event
        data object InstallClicked : Event
    }

    sealed interface Effect : UiEffect {
        data object RequestHealthPermission : Effect
        data object OpenHealthConnectInstall : Effect
    }
}

@Immutable
data class TodayRecordUiModel(
    val distance: String,
    val pace: String,
    val time: String
)

@Immutable
data class PastRecordUiModel(
    val id: String,
    val date: String,
    val distance: String,
    val pace: String,
    val time: String
)
