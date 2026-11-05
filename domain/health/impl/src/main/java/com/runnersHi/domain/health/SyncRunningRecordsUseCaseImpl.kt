package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.repository.RunningRecordRepository
import com.runnersHi.domain.health.usecase.SyncRunningRecordsUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 서버는 같은 기록을 두 번 받아도 누적 거리에 더한다.
 * 그래서 서버 기록과 시작 시각을 대조해 없는 것만 올리고, 동시에 두 번 돌지 않게 막는다.
 */
@Singleton
class SyncRunningRecordsUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository,
    private val runningRecordRepository: RunningRecordRepository
) : SyncRunningRecordsUseCase {

    private val mutex = Mutex()

    override suspend fun invoke(): Result<Int> = mutex.withLock {
        runCatching {
            val now = Instant.now()
            val since = now.minus(SYNC_WINDOW)
            val records = healthRepository.getExerciseRecords(since, now).getOrThrow()
                .filter { record ->
                    record.exerciseType == ExerciseType.RUNNING &&
                        (record.distanceMeters ?: 0.0) >= MIN_DISTANCE_METERS &&
                        record.durationMillis >= MIN_DURATION_MILLIS
                }
            if (records.isEmpty()) return@runCatching 0

            val uploaded = runningRecordRepository.uploadedStartTimes(since)
            val pending = records.filter { it.startTime.truncatedTo(ChronoUnit.SECONDS) !in uploaded }
            pending.forEach { runningRecordRepository.upload(it) }
            pending.size
        }
    }

    private companion object {
        val SYNC_WINDOW: Duration = Duration.ofDays(30)
        const val MIN_DISTANCE_METERS = 1.0
        const val MIN_DURATION_MILLIS = 1000L
    }
}
