package com.runnersHi.domain.health.repository

import com.runnersHi.domain.health.model.ExerciseRecord
import java.time.Instant

/** 서버에 올린 러닝 기록 */
interface RunningRecordRepository {
    /** since 이후 서버에 올린 기록의 시작 시각(초 단위) */
    suspend fun uploadedStartTimes(since: Instant): Set<Instant>
    suspend fun upload(record: ExerciseRecord)

    /** since 이후 시작한 서버 기록. 폰을 바꾸면 Health Connect에는 없고 서버에만 남는다. */
    suspend fun records(since: Instant): List<ExerciseRecord>
}
