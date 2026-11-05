package com.runnersHi.presentation.record

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class RecordFormattersTest {

    @Test
    fun `거리는 오늘은 한 자리, 지난 기록은 두 자리로 보여준다`() {
        assertEquals("7.4 km", RecordFormatters.todayDistance(7_400.0))
        assertEquals("7.40 km", RecordFormatters.pastDistance(7_400.0))
    }

    @Test
    fun `페이스는 1km당 시간이다`() {
        assertEquals("5’00’’", RecordFormatters.pace(5_000.0, 25 * 60_000L))
        assertEquals("5’30’’", RecordFormatters.pace(1_000.0, 330_000L))
    }

    @Test
    fun `거리나 시간이 없으면 페이스는 빈 값이다`() {
        assertEquals("-", RecordFormatters.pace(0.0, 1_000L))
        assertEquals("-", RecordFormatters.pace(1_000.0, 0L))
    }

    @Test
    fun `시간은 1시간부터 시 단위를 붙인다`() {
        assertEquals("59:59", RecordFormatters.time(3_599_000L))
        assertEquals("1:00:00", RecordFormatters.time(3_600_000L))
    }

    @Test
    fun `날짜는 한국어 요일과 함께 보여준다`() {
        val date = RecordFormatters.date(Instant.parse("2026-11-04T01:00:00Z"), ZoneOffset.UTC)

        assertEquals("2026.11.04 수", date)
    }
}
