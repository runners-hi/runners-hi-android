package com.runnersHi.presentation.main

import androidx.compose.runtime.Immutable
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState

object MainContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val tierInfo: TierInfoUiModel? = null,
        val todaysRun: TodaysRunUiModel? = null,
        val thisWeek: ThisWeekUiModel? = null,
        val missionEvent: MissionEventUiModel? = null,
        val errorMessage: String? = null,
        val currentTab: BottomNavTab = BottomNavTab.HOME
    ) : UiState

    sealed interface Event : UiEvent {
        data object LoadData : Event
        data object RefreshData : Event
        data object TierCardClicked : Event
        data object TodaysRunClicked : Event
        data object MissionEventClicked : Event
        data class MissionItemClicked(val id: String) : Event
        data class BottomNavClicked(val tab: BottomNavTab) : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToTierDetail : Effect
        data object NavigateToTodaysRunDetail : Effect
        data object NavigateToMissionEvent : Effect
        data class NavigateToMissionDetail(val id: String) : Effect
        data class ShowToast(val message: String) : Effect
    }
}

enum class BottomNavTab {
    HOME, RANKING, RECORD, MISSION, MY_PAGE
}

@Immutable
data class TierInfoUiModel(
    val tier: Tier,
    val tierName: String,
    val level: String,
    val progressPercent: Int,
    val progressText: String
)

@Immutable
data class TodaysRunUiModel(
    val distance: String,
    val pace: String,
    val time: String
)

@Immutable
data class ThisWeekUiModel(
    val totalDistance: String,
    val days: List<DayUiModel>
)

@Immutable
data class DayUiModel(
    val label: String,
    val distance: String?,
    val hasRun: Boolean
)

@Immutable
data class MissionEventUiModel(
    val banner: EventBannerUiModel?,
    val missions: List<MissionItemUiModel>
)

@Immutable
data class EventBannerUiModel(
    val title: String,
    val period: String
)

@Immutable
data class MissionItemUiModel(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val isCompleted: Boolean
)
