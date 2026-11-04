package com.runnersHi.data.health

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class MockHealthDataSourceTest {

    private val today = LocalDate.of(2026, 11, 4)
    private val dataSource = MockHealthDataSource(clock = { today }, zone = ZoneOffset.UTC)

    private fun startOf(date: LocalDate) = date.atStartOfDay(ZoneOffset.UTC).toInstant()

    @Test
    fun `오늘 범위에는 오늘 기록만 들어온다`() = runTest {
        val records = dataSource.getExerciseRecords(startOf(today), startOf(today.plusDays(1)))

        assertEquals(1, records.size)
        assertEquals(7_400.0, records.single().distanceMeters!!, 0.0)
        assertEquals(40 * 60 * 1000L, records.single().durationMillis)
    }

    @Test
    fun `전체 범위에는 7건이 모두 들어온다`() = runTest {
        val records = dataSource.getExerciseRecords(startOf(today.minusDays(30)), startOf(today.plusDays(1)))

        assertEquals(7, records.size)
    }

    @Test
    fun `범위 밖 기록은 제외한다`() = runTest {
        val records = dataSource.getExerciseRecords(startOf(today.minusDays(2)), startOf(today.minusDays(1)))

        assertEquals(0, records.size)
    }
}
