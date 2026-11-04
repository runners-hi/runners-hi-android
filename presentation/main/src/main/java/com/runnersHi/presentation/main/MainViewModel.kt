package com.runnersHi.presentation.main

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.auth.usecase.GetStoredTokenUseCase
import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.usecase.CheckHealthPermissionUseCase
import com.runnersHi.domain.health.usecase.GetTodayRunningDataUseCase
import com.runnersHi.domain.health.usecase.GetWeekRunningDataUseCase
import com.runnersHi.domain.home.model.HomeData
import com.runnersHi.domain.home.usecase.GetHomeDataUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStoredTokenUseCase: GetStoredTokenUseCase,
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val checkHealthPermissionUseCase: CheckHealthPermissionUseCase,
    private val getTodayRunningDataUseCase: GetTodayRunningDataUseCase,
    private val getWeekRunningDataUseCase: GetWeekRunningDataUseCase
) : MviViewModel<MainContract.State, MainContract.Event, MainContract.Effect>(
    MainContract.State()
) {

    init {
        // 권한 확인이 끝난 뒤 데이터를 불러와야 기록을 읽을 수 있다.
        handleEvent(MainContract.Event.CheckHealthPermission)
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
                sendEffect(MainContract.Effect.ShowToast("Health Connect가 없어 러닝 기록을 불러올 수 없어요"))
            }
            loadData()
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

            val token = getStoredTokenUseCase()
            if (token == null) {
                updateState { copy(isLoading = false, errorMessage = "로그인 정보가 없습니다.") }
                return@launch
            }

            // Load home data (tier, missions, etc.)
            val homeDataResult = async { getHomeDataUseCase(token.accessToken) }

            // Load health data if permission is granted
            val hasHealthPermission = state.value.healthPermissionGranted
            val todayHealthData = if (hasHealthPermission) {
                async { getTodayRunningDataUseCase() }
            } else null
            val weekHealthData = if (hasHealthPermission) {
                async { getWeekRunningDataUseCase() }
            } else null

            homeDataResult.await()
                .onSuccess { homeData ->
                    // Get health data results
                    val todayRecords = todayHealthData?.await()?.getOrNull() ?: emptyList()
                    val weekRecords = weekHealthData?.await()?.getOrNull() ?: emptyList()

                    updateState {
                        copy(
                            isLoading = false,
                            isTodayEmpty = todayRecords.isEmpty(),
                            isWeekEmpty = weekRecords.isEmpty(),
                            tierInfo = homeData.toTierInfoUiModel(),
                            todaysRun = todayRecords.takeIf { it.isNotEmpty() }?.toTodaysRunUiModel(),
                            thisWeek = weekRecords.takeIf { it.isNotEmpty() }?.toThisWeekUiModel(),
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

    private fun List<ExerciseRecord>.toTodaysRunUiModel(): TodaysRunUiModel {
        val totalDistanceMeters = sumOf { it.distanceMeters ?: 0.0 }
        val totalDurationMillis = sumOf { it.durationMillis }

        val distanceKm = totalDistanceMeters / 1000.0
        val totalMinutes = (totalDurationMillis / 1000 / 60).toInt()
        val totalSeconds = ((totalDurationMillis / 1000) % 60).toInt()

        // Calculate pace (minutes per km)
        val paceMinutes: Int
        val paceSeconds: Int
        if (distanceKm > 0) {
            val totalPaceSeconds = (totalDurationMillis / 1000.0 / distanceKm).toInt()
            paceMinutes = totalPaceSeconds / 60
            paceSeconds = totalPaceSeconds % 60
        } else {
            paceMinutes = 0
            paceSeconds = 0
        }

        return TodaysRunUiModel(
            distance = String.format("%.1f km", distanceKm),
            pace = "${paceMinutes}'${paceSeconds.toString().padStart(2, '0')}''",
            time = "${totalMinutes}:${totalSeconds.toString().padStart(2, '0')}"
        )
    }

    private fun List<ExerciseRecord>.toThisWeekUiModel(): ThisWeekUiModel {
        val totalDistanceMeters = sumOf { it.distanceMeters ?: 0.0 }
        val totalDistanceKm = totalDistanceMeters / 1000.0

        // Group records by day of week
        val zone = ZoneId.systemDefault()
        val recordsByDay = groupBy { record ->
            record.startTime.atZone(zone).toLocalDate().dayOfWeek
        }

        val today = LocalDate.now(zone)
        val weekStart = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))

        val days = listOf(
            java.time.DayOfWeek.MONDAY,
            java.time.DayOfWeek.TUESDAY,
            java.time.DayOfWeek.WEDNESDAY,
            java.time.DayOfWeek.THURSDAY,
            java.time.DayOfWeek.FRIDAY,
            java.time.DayOfWeek.SATURDAY,
            java.time.DayOfWeek.SUNDAY
        ).map { dayOfWeek ->
            val dayRecords = recordsByDay[dayOfWeek] ?: emptyList()
            val dayDistance = dayRecords.sumOf { it.distanceMeters ?: 0.0 } / 1000.0
            val hasRun = dayRecords.isNotEmpty()

            DayUiModel(
                label = dayOfWeek.toShortLabel(),
                distance = if (hasRun) {
                    if (dayDistance >= 10) dayDistance.toInt().toString() else String.format("%.1f", dayDistance)
                } else null,
                hasRun = hasRun
            )
        }

        return ThisWeekUiModel(
            totalDistance = String.format("%.1f km", totalDistanceKm),
            days = days
        )
    }

    private fun java.time.DayOfWeek.toShortLabel(): String {
        return when (this) {
            java.time.DayOfWeek.MONDAY -> "M"
            java.time.DayOfWeek.TUESDAY -> "T"
            java.time.DayOfWeek.WEDNESDAY -> "W"
            java.time.DayOfWeek.THURSDAY -> "T"
            java.time.DayOfWeek.FRIDAY -> "F"
            java.time.DayOfWeek.SATURDAY -> "S"
            java.time.DayOfWeek.SUNDAY -> "S"
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
                    iconName = mission.iconName,
                    isCompleted = mission.isCompleted
                )
            }
        )
    }
}
