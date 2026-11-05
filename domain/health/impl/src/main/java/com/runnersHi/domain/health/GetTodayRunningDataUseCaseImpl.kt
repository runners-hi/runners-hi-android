package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.usecase.GetTodayRunningDataUseCase
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetTodayRunningDataUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository,
    private val merger: RunningRecordMerger
) : GetTodayRunningDataUseCase {
    override suspend fun invoke(): Result<List<ExerciseRecord>> {
        val zone = ZoneId.systemDefault()
        val startOfDay = LocalDate.now(zone).atStartOfDay(zone).toInstant()
        return merger.merge(healthRepository.getTodayRunningRecords(), startOfDay)
    }
}
