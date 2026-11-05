package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.model.HealthPermissionStatus
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.repository.RunningRecordRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class SyncRunningRecordsUseCaseImplTest {

    private val base = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS)

    private fun record(
        id: String,
        start: Instant,
        type: ExerciseType = ExerciseType.RUNNING,
        meters: Double? = 5000.0,
        millis: Long = 1_800_000
    ) = ExerciseRecord(id, start, start.plusMillis(millis), type, meters, millis, 300.0)

    private class FakeHealth(private val records: List<ExerciseRecord>) : HealthRepository {
        override suspend fun checkPermissionStatus() = HealthPermissionStatus(true, true, false)
        override suspend fun getExerciseRecords(startTime: Instant, endTime: Instant) = Result.success(records)
        override suspend fun getTodayRunningRecords() = Result.success(records)
        override suspend fun getWeekRunningRecords() = Result.success(records)
    }

    private class FakeServer(val uploadedAt: MutableSet<Instant> = mutableSetOf()) : RunningRecordRepository {
        val posted = mutableListOf<String>()
        override suspend fun uploadedStartTimes(since: Instant) = uploadedAt.toSet()
        override suspend fun upload(record: ExerciseRecord) {
            posted += record.id
            uploadedAt += record.startTime
        }
    }

    @Test
    fun `서버에 없는 기록만 올린다`() = runTest {
        val server = FakeServer(mutableSetOf(base))
        val useCase = SyncRunningRecordsUseCaseImpl(
            FakeHealth(listOf(record("old", base.plusMillis(300)), record("new", base.plusSeconds(3600)))),
            server
        )

        assertEquals(1, useCase().getOrThrow())
        assertEquals(listOf("new"), server.posted)
    }

    @Test
    fun `두 번 돌려도 중복으로 올리지 않는다`() = runTest {
        val server = FakeServer()
        val useCase = SyncRunningRecordsUseCaseImpl(FakeHealth(listOf(record("a", base))), server)

        useCase()
        assertEquals(0, useCase().getOrThrow())
        assertEquals(listOf("a"), server.posted)
    }

    @Test
    fun `러닝이 아니거나 서버 검증에 걸리는 기록은 올리지 않는다`() = runTest {
        val server = FakeServer()
        val useCase = SyncRunningRecordsUseCaseImpl(
            FakeHealth(
                listOf(
                    record("walk", base, ExerciseType.WALKING),
                    record("nodist", base.plusSeconds(10), meters = null),
                    record("short", base.plusSeconds(20), millis = 100)
                )
            ),
            server
        )

        assertEquals(0, useCase().getOrThrow())
        assertTrue(server.posted.isEmpty())
    }

    @Test
    fun `서버 오류는 실패로 돌려준다`() = runTest {
        val server = object : RunningRecordRepository {
            override suspend fun uploadedStartTimes(since: Instant): Set<Instant> = error("500")
            override suspend fun upload(record: ExerciseRecord) = Unit
        }
        val useCase = SyncRunningRecordsUseCaseImpl(FakeHealth(listOf(record("a", base))), server)

        assertTrue(useCase().isFailure)
    }
}
