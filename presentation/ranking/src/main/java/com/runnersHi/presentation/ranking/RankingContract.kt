package com.runnersHi.presentation.ranking

import androidx.compose.runtime.Immutable
import com.runnersHi.domain.ranking.model.RankingEntry
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object RankingContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val regionName: String = "",
        val topEntries: List<RankingEntry> = emptyList(),
        val myEntry: RankingEntry? = null,
        val errorMessage: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
    }

    sealed interface Effect : UiEffect
}
