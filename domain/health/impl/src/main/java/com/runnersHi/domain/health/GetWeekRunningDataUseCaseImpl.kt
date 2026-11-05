package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.usecase.GetWeekRunningDataUseCase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class GetWeekRunningDataUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository,
    private val merger: RunningRecordMerger
) : GetWeekRunningDataUseCase {
    override suspend fun invoke(): Result<List<ExerciseRecord>> {
        val zone = ZoneId.systemDefault()
        val startOfWeek = LocalDate.now(zone)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay(zone).toInstant()
        return merger.merge(healthRepository.getWeekRunningRecords(), startOfWeek)
    }
}
