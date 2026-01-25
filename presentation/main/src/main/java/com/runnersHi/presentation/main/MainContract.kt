package com.runnersHi.presentation.main

import androidx.compose.runtime.Immutable
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.presentation.common.mvi.UiEffect
import com.runnersHi.presentation.common.mvi.UiEvent
import com.runnersHi.presentation.common.mvi.UiState
import com.runnersHi.presentation.common.navigation.BottomNavTab

object MainContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val tierInfo: TierInfoUiModel? = null,
        val todaysRun: TodaysRunUiModel? = null,
        val thisWeek: ThisWeekUiModel? = null,
        val missionEvent: MissionEventUiModel? = null,
        val errorMessage: String? = null,
        val currentTab: BottomNavTab = BottomNavTab.HOME,
        // Empty State
        val isEmptyState: Boolean = false,
        // Tier Bottom Sheet
        val showTierInfoSheet: Boolean = false,
        val tierGuideList: List<TierGuideItem> = defaultTierGuideList,
        // Health Connect
        val healthConnectAvailable: Boolean = false,
        val healthPermissionGranted: Boolean = false,
        val needsHealthPermission: Boolean = false
    ) : UiState

    sealed interface Event : UiEvent {
        data object LoadData : Event
        data object RefreshData : Event
        data object TierCardClicked : Event
        data object TierArrowClicked : Event
        data object TierSheetDismissed : Event
        data object TodaysRunClicked : Event
        data object MissionEventClicked : Event
        data class MissionItemClicked(val id: String) : Event
        data class BottomNavClicked(val tab: BottomNavTab) : Event
        // Health Connect
        data object CheckHealthPermission : Event
        data class HealthPermissionResult(val granted: Boolean) : Event
    }

    sealed interface Effect : UiEffect {
        data object NavigateToTierDetail : Effect
        data object NavigateToTodaysRunDetail : Effect
        data object NavigateToMissionEvent : Effect
        data class NavigateToMissionDetail(val id: String) : Effect
        data class ShowToast(val message: String) : Effect
        // Health Connect
        data object RequestHealthPermission : Effect
        data object OpenHealthConnectSettings : Effect
        data object OpenPlayStoreForHealthConnect : Effect
    }
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
    val isCompleted: Boolean,
    val iconWidthDp: Int = 60,
    val iconHeightDp: Int = 60
)

// Tier Guide models
@Immutable
data class TierGuideItem(
    val tier: Tier,
    val tierName: String,
    val levelRange: String
)

val defaultTierGuideList = listOf(
    TierGuideItem(Tier.BRONZE, "Bronze Runner", "Level 1 - Level 5"),
    TierGuideItem(Tier.SILVER, "Silver Runner", "Level 6 - Level 20"),
    TierGuideItem(Tier.GOLD, "Gold Runner", "Level 21 - Level 40"),
    TierGuideItem(Tier.PLATINUM, "Platinum Runner", "Level 41 - Level 70"),
    TierGuideItem(Tier.DIAMOND, "Diamond Runner", "Level 71 - Level 100")
)

val tierInfoTexts = listOf(
    "러너 티어는 매년 1월 1일에 초기화됩니다.",
    "한 해 동안 쌓은 점수를 기준으로 12월 마지막 주에 최종 티어가 확정되며, 확정된 티어의 뱃지는 미션함으로 지급됩니다.",
    "연중에는 모든 러너가 브론즈 Level 1에서 시작하며, 점수를 달성할 때마다 레벨업과 상위 티어 승급이 가능합니다.",
    "티어는 하향되지 않고, 오직 승급만 할 수 있습니다.",
    "신규 가입자는 자동으로 브론즈 Level 1에서 시작하며, GPS 조작 등 부정 기록은 무효 처리됩니다.",
    "추후 등급별 선정 기준이나 혜택은 변경될 수 있습니다."
)
