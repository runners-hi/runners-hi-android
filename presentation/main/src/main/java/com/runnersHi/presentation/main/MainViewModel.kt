package com.runnersHi.presentation.main

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.health.usecase.CheckHealthPermissionUseCase
import com.runnersHi.domain.health.usecase.GetTodayRunningDataUseCase
import com.runnersHi.domain.health.usecase.GetWeekRunningDataUseCase
import com.runnersHi.domain.home.model.DayOfWeek
import com.runnersHi.domain.home.model.HomeData
import com.runnersHi.domain.home.usecase.GetHomeDataUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val checkHealthPermissionUseCase: CheckHealthPermissionUseCase,
    private val getTodayRunningDataUseCase: GetTodayRunningDataUseCase,
    private val getWeekRunningDataUseCase: GetWeekRunningDataUseCase
) : MviViewModel<MainContract.State, MainContract.Event, MainContract.Effect>(
    MainContract.State()
) {

    init {
        handleEvent(MainContract.Event.CheckHealthPermission)
        handleEvent(MainContract.Event.LoadData)
    }

    override fun handleEvent(event: MainContract.Event) {
        when (event) {
            is MainContract.Event.LoadData -> loadData()
            is MainContract.Event.RefreshData -> loadData()
            is MainContract.Event.TierCardClicked -> {
                sendEffect(MainContract.Effect.NavigateToTierDetail)
            }
            is MainContract.Event.TierArrowClicked -> {
                updateState { copy(showTierInfoSheet = true) }
            }
            is MainContract.Event.TierSheetDismissed -> {
                updateState { copy(showTierInfoSheet = false) }
            }
            is MainContract.Event.TodaysRunClicked -> {
                sendEffect(MainContract.Effect.NavigateToTodaysRunDetail)
            }
            is MainContract.Event.MissionEventClicked -> {
                sendEffect(MainContract.Effect.NavigateToMissionEvent)
            }
            is MainContract.Event.MissionItemClicked -> {
                sendEffect(MainContract.Effect.NavigateToMissionDetail(event.id))
            }
            is MainContract.Event.BottomNavClicked -> {
                updateState { copy(currentTab = event.tab) }
            }
            is MainContract.Event.CheckHealthPermission -> {
                checkHealthPermission()
            }
            is MainContract.Event.HealthPermissionResult -> {
                handleHealthPermissionResult(event.granted)
            }
        }
    }

    private fun checkHealthPermission() {
        viewModelScope.launch {
            val status = checkHealthPermissionUseCase()
            updateState {
                copy(
                    healthConnectAvailable = status.isAvailable,
                    healthPermissionGranted = status.hasReadPermission,
                    needsHealthPermission = status.needsPermissionRequest
                )
            }

            if (status.needsPermissionRequest) {
                sendEffect(MainContract.Effect.RequestHealthPermission)
            } else if (!status.isAvailable) {
                // Health Connect not available, show play store option
                sendEffect(MainContract.Effect.OpenPlayStoreForHealthConnect)
            }
        }
    }

    private fun handleHealthPermissionResult(granted: Boolean) {
        updateState {
            copy(
                healthPermissionGranted = granted,
                needsHealthPermission = !granted
            )
        }

        if (granted) {
            // Reload data with health connect data
            handleEvent(MainContract.Event.RefreshData)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            // TODO: 실제로는 토큰 관리자에서 가져와야 함
            val mockToken = "mock_user_token"

            getHomeDataUseCase(mockToken)
                .onSuccess { homeData ->
                    val isEmpty = homeData.todaysRun.distanceKm == 0.0 &&
                        homeData.thisWeek.totalDistanceKm == 0.0
                    updateState {
                        copy(
                            isLoading = false,
                            isEmptyState = isEmpty,
                            tierInfo = homeData.toTierInfoUiModel(),
                            todaysRun = homeData.toTodaysRunUiModel(),
                            thisWeek = homeData.toThisWeekUiModel(),
                            missionEvent = homeData.toMissionEventUiModel()
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "데이터를 불러오는데 실패했습니다."
                        )
                    }
                }
        }
    }

    private fun HomeData.toTierInfoUiModel(): TierInfoUiModel {
        return TierInfoUiModel(
            tier = tier.tier,
            tierName = tier.tierName,
            level = "Level ${tier.level}",
            progressPercent = tier.progressPercent,
            progressText = "${tier.progressPercent}%"
        )
    }

    private fun HomeData.toTodaysRunUiModel(): TodaysRunUiModel {
        return TodaysRunUiModel(
            distance = "${todaysRun.distanceKm} km",
            pace = "${todaysRun.paceMinutes}'${todaysRun.paceSeconds.toString().padStart(2, '0')}''",
            time = "${todaysRun.timeMinutes}:${todaysRun.timeSeconds.toString().padStart(2, '0')}"
        )
    }

    private fun HomeData.toThisWeekUiModel(): ThisWeekUiModel {
        return ThisWeekUiModel(
            totalDistance = "${thisWeek.totalDistanceKm} km",
            days = thisWeek.dailyRecords.map { record ->
                DayUiModel(
                    label = record.dayOfWeek.toLabel(),
                    distance = record.distanceKm?.let {
                        if (it >= 10) it.toInt().toString() else String.format("%.1f", it)
                    },
                    hasRun = record.hasRun
                )
            }
        )
    }

    private fun DayOfWeek.toLabel(): String {
        return when (this) {
            DayOfWeek.MONDAY -> "M"
            DayOfWeek.TUESDAY -> "T"
            DayOfWeek.WEDNESDAY -> "W"
            DayOfWeek.THURSDAY -> "T"
            DayOfWeek.FRIDAY -> "F"
            DayOfWeek.SATURDAY -> "S"
            DayOfWeek.SUNDAY -> "S"
        }
    }

    private fun HomeData.toMissionEventUiModel(): MissionEventUiModel {
        return MissionEventUiModel(
            banner = missionEvent.eventBanner?.let { banner ->
                EventBannerUiModel(
                    title = banner.title,
                    period = banner.period
                )
            },
            missions = missionEvent.missions.map { mission ->
                MissionItemUiModel(
                    id = mission.id,
                    name = mission.name,
                    description = mission.description,
                    imageUrl = mission.imageUrl,
                    isCompleted = mission.isCompleted
                )
            }
        )
    }
}
