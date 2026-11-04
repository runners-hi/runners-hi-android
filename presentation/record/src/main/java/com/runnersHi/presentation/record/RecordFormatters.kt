package com.runnersHi.presentation.record

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal object RecordFormatters {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd E", Locale.KOREA)

    fun todayDistance(meters: Double): String = String.format(Locale.US, "%.1f km", meters / 1000)

    fun pastDistance(meters: Double): String = String.format(Locale.US, "%.2f km", meters / 1000)

    /** 1km당 페이스. 거리가 없으면 "-" */
    fun pace(meters: Double, durationMillis: Long): String {
        if (meters <= 0.0 || durationMillis <= 0L) return "-"
        val secondsPerKm = (durationMillis / 1000.0 / (meters / 1000)).toInt()
        return String.format(Locale.US, "%d'%02d''", secondsPerKm / 60, secondsPerKm % 60)
    }

    /** m:ss, 1시간 이상이면 h:mm:ss */
    fun time(durationMillis: Long): String {
        val total = durationMillis / 1000
        val hours = total / 3600
        val minutes = (total % 3600) / 60
        val seconds = total % 60
        return if (hours > 0) {
            String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%d:%02d", minutes, seconds)
        }
    }

    fun date(instant: Instant, zone: ZoneId): String = instant.atZone(zone).format(dateFormatter)
}
