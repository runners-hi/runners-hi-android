package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.usecase.GetRunningRecordsUseCase
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class GetRunningRecordsUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository,
    private val merger: RunningRecordMerger
) : GetRunningRecordsUseCase {

    override suspend fun invoke(): Result<List<ExerciseRecord>> {
        val now = Instant.now()
        val local = healthRepository.getExerciseRecords(now.minus(Duration.ofDays(HEALTH_CONNECT_DAYS)), now)
        return merger.merge(local, now.minus(Duration.ofDays(HISTORY_DAYS)))
    }

    private companion object {
        // Health Connect가 기본으로 읽기를 허용하는 기간
        const val HEALTH_CONNECT_DAYS = 30L
        // 그보다 오래된 기록은 서버에서 읽는다
        const val HISTORY_DAYS = 365L
    }
}
