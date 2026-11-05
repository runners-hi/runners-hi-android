package com.runnersHi.data.health

import com.runnersHi.core.network.RunningRecordCreateRequest
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.core.network.apiCallNullable
import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.repository.RunningRecordRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

/** 서버는 기기 시간대 기준 LocalDateTime을 저장한다. */
class RemoteRunningRecordRepository @Inject constructor(
    private val api: RunnersHiApi
) : RunningRecordRepository {

    override suspend fun uploadedStartTimes(since: Instant): Set<Instant> {
        val zone = ZoneId.systemDefault()
        val startTimes = mutableSetOf<Instant>()
        var cursor: Long? = null
        do {
            val page = apiCall { api.getRunningRecords(cursor, PAGE_SIZE) }
            val times = page.records.map {
                LocalDateTime.parse(it.startedAt).atZone(zone).toInstant().truncatedTo(ChronoUnit.SECONDS)
            }
            startTimes += times
            cursor = page.nextCursor
            // 목록은 최신순이므로 기간을 벗어나면 그만 읽는다.
            val reachedWindow = times.any { it.isBefore(since) }
        } while (page.hasNext && cursor != null && !reachedWindow)
        return startTimes
    }

    override suspend fun upload(record: ExerciseRecord) {
        val zone = ZoneId.systemDefault()
        val request = RunningRecordCreateRequest(
            distance = (record.distanceMeters ?: 0.0).roundToInt(),
            duration = (record.durationMillis / 1000).toInt(),
            calories = record.calories?.roundToInt(),
            startedAt = record.startTime.format(zone),
            endedAt = record.endTime.format(zone),
            source = HEALTH_CONNECT
        )
        apiCallNullable { api.createRunningRecord(request) }
    }

    private fun Instant.format(zone: ZoneId): String =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
            truncatedTo(ChronoUnit.SECONDS).atZone(zone).toLocalDateTime()
        )

    private companion object {
        const val PAGE_SIZE = 50
        const val HEALTH_CONNECT = "HEALTH_CONNECT"
    }
}

/** 서버 주소가 없을 때: 올리지 않는다. */
class NoOpRunningRecordRepository @Inject constructor() : RunningRecordRepository {
    override suspend fun uploadedStartTimes(since: Instant): Set<Instant> = emptySet()
    override suspend fun upload(record: ExerciseRecord) = Unit
}
