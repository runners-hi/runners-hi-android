package com.runnersHi.presentation.record

import androidx.lifecycle.viewModelScope
import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.usecase.CheckHealthPermissionUseCase
import com.runnersHi.domain.health.usecase.GetRunningRecordsUseCase
import com.runnersHi.presentation.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val checkHealthPermissionUseCase: CheckHealthPermissionUseCase,
    private val getRunningRecordsUseCase: GetRunningRecordsUseCase
) : MviViewModel<RecordContract.State, RecordContract.Event, RecordContract.Effect>(
    RecordContract.State()
) {

    override fun handleEvent(event: RecordContract.Event) {
        when (event) {
            RecordContract.Event.Refresh -> refresh()
            RecordContract.Event.GrantPermissionClicked ->
                sendEffect(RecordContract.Effect.RequestHealthPermission)
            RecordContract.Event.InstallClicked ->
                sendEffect(RecordContract.Effect.OpenHealthConnectInstall)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val status = checkHealthPermissionUseCase()
            val access = when {
                !status.isAvailable -> RecordContract.Access.NEEDS_INSTALL
                !status.hasReadPermission -> RecordContract.Access.NEEDS_PERMISSION
                else -> RecordContract.Access.READY
            }
            if (access != RecordContract.Access.READY) {
                updateState { copy(isLoading = false, access = access, errorMessage = null) }
                return@launch
            }

            getRunningRecordsUseCase()
                .onSuccess { records -> showRecords(records) }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            access = access,
                            errorMessage = error.message ?: "기록을 불러오는데 실패했습니다."
                        )
                    }
                }
        }
    }

    private fun showRecords(records: List<ExerciseRecord>) {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val (todayRecords, pastRecords) = records.partition {
            it.startTime.atZone(zone).toLocalDate() == today
        }
        updateState {
            copy(
                isLoading = false,
                access = RecordContract.Access.READY,
                errorMessage = null,
                today = todayRecords.takeIf { it.isNotEmpty() }?.toToday(),
                past = pastRecords.map { it.toPast(zone) }
            )
        }
    }

    private fun List<ExerciseRecord>.toToday(): TodayRecordUiModel {
        val meters = sumOf { it.distanceMeters ?: 0.0 }
        val millis = sumOf { it.durationMillis }
        return TodayRecordUiModel(
            distance = RecordFormatters.todayDistance(meters),
            pace = RecordFormatters.pace(meters, millis),
            time = RecordFormatters.time(millis)
        )
    }

    private fun ExerciseRecord.toPast(zone: ZoneId): PastRecordUiModel {
        val meters = distanceMeters ?: 0.0
        return PastRecordUiModel(
            id = id,
            date = RecordFormatters.date(startTime, zone),
            distance = RecordFormatters.pastDistance(meters),
            pace = RecordFormatters.pace(meters, durationMillis),
            time = RecordFormatters.time(durationMillis)
        )
    }
}
