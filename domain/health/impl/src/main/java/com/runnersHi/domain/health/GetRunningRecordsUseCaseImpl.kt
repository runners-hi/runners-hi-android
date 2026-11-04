package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.usecase.GetRunningRecordsUseCase
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class GetRunningRecordsUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository
) : GetRunningRecordsUseCase {

    override suspend fun invoke(): Result<List<ExerciseRecord>> {
        val now = Instant.now()
        return healthRepository
            .getExerciseRecords(now.minus(Duration.ofDays(RECENT_DAYS)), now)
            .map { records ->
                records
                    .filter { it.exerciseType == ExerciseType.RUNNING }
                    .sortedByDescending { it.startTime }
            }
    }

    private companion object {
        // Health Connect가 기본으로 읽기를 허용하는 기간
        const val RECENT_DAYS = 30L
    }
}
