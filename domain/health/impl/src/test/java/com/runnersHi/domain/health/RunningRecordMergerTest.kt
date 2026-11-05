package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.repository.RunningRecordRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class RunningRecordMergerTest {

    private val base = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS)
    private val since = base.minus(1, ChronoUnit.DAYS)

    private fun record(id: String, start: Instant, type: ExerciseType = ExerciseType.RUNNING) =
        ExerciseRecord(id, start, start.plusSeconds(1800), type, 5000.0, 1_800_000, 300.0)

    private fun server(vararg records: ExerciseRecord) = object : RunningRecordRepository {
        override suspend fun uploadedStartTimes(since: Instant) = emptySet<Instant>()
        override suspend fun upload(record: ExerciseRecord) = Unit
        override suspend fun records(since: Instant) = records.toList()
    }

    private val brokenServer = object : RunningRecordRepository {
        override suspend fun uploadedStartTimes(since: Instant) = emptySet<Instant>()
        override suspend fun upload(record: ExerciseRecord) = Unit
        override suspend fun records(since: Instant): List<ExerciseRecord> = error("500")
    }

    @Test
    fun `기기에 없는 서버 기록을 합쳐 최신순으로 돌려준다`() = runTest {
        val merged = RunningRecordMerger(server(record("server-1", base.minusSeconds(3600))))
            .merge(Result.success(listOf(record("hc", base))), since).getOrThrow()

        assertEquals(listOf("hc", "server-1"), merged.map { it.id })
    }

    @Test
    fun `양쪽에 있는 기록은 초 단위로 대조해 기기 값 한 건만 남긴다`() = runTest {
        val merged = RunningRecordMerger(server(record("server-1", base)))
            .merge(Result.success(listOf(record("hc", base.plusMillis(300)))), since).getOrThrow()

        assertEquals(listOf("hc"), merged.map { it.id })
    }

    @Test
    fun `러닝이 아닌 기기 기록과 기간 밖 서버 기록은 뺀다`() = runTest {
        val merged = RunningRecordMerger(server(record("old", since.minusSeconds(1))))
            .merge(Result.success(listOf(record("walk", base, ExerciseType.WALKING))), since).getOrThrow()

        assertTrue(merged.isEmpty())
    }

    @Test
    fun `서버가 실패해도 기기 기록은 보여 준다`() = runTest {
        val merged = RunningRecordMerger(brokenServer)
            .merge(Result.success(listOf(record("hc", base))), since).getOrThrow()

        assertEquals(listOf("hc"), merged.map { it.id })
    }

    @Test
    fun `기기가 실패해도 서버 기록은 보여 준다`() = runTest {
        val merged = RunningRecordMerger(server(record("server-1", base)))
            .merge(Result.failure(IllegalStateException("권한 없음")), since).getOrThrow()

        assertEquals(listOf("server-1"), merged.map { it.id })
    }

    @Test
    fun `둘 다 실패하면 실패로 돌려준다`() = runTest {
        val merged = RunningRecordMerger(brokenServer)
            .merge(Result.failure(IllegalStateException("권한 없음")), since)

        assertTrue(merged.isFailure)
    }
}
