package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.repository.RunningRecordRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * 이 기기의 Health Connect 기록과 서버 기록을 합친다.
 * 올린 기록은 양쪽에 있으므로 시작 시각(초 단위)으로 한 건만 남기고, 더 정밀한 기기 값을 쓴다.
 * 한쪽만 실패하면 다른 쪽을 보여 주고, 둘 다 실패할 때만 실패로 돌려준다.
 */
class RunningRecordMerger @Inject constructor(
    private val runningRecordRepository: RunningRecordRepository
) {

    suspend fun merge(local: Result<List<ExerciseRecord>>, since: Instant): Result<List<ExerciseRecord>> {
        val server = runCatching { runningRecordRepository.records(since) }
        if (local.isFailure && server.isFailure) return local

        val running = local.getOrDefault(emptyList()).filter { it.exerciseType == ExerciseType.RUNNING }
        val localStarts = running.map { it.startTime.truncatedTo(ChronoUnit.SECONDS) }.toSet()
        val serverOnly = server.getOrDefault(emptyList()).filter {
            it.startTime.truncatedTo(ChronoUnit.SECONDS) !in localStarts && !it.startTime.isBefore(since)
        }
        return Result.success((running + serverOnly).sortedByDescending { it.startTime })
    }
}
