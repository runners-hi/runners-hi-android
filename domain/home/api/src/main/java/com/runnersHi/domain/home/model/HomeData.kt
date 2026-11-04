package com.runnersHi.domain.home.model

data class HomeData(
    val tier: TierInfo,
    val todaysRun: TodaysRun,
    val thisWeek: ThisWeekData
)

data class TierInfo(
    val tier: Tier,
    val tierName: String,
    val level: Int,
    val progressPercent: Int
)

data class TodaysRun(
    val distanceKm: Double,
    val paceMinutes: Int,
    val paceSeconds: Int,
    val timeMinutes: Int,
    val timeSeconds: Int
)

data class ThisWeekData(
    val totalDistanceKm: Double,
    val dailyRecords: List<DailyRecord>
)

data class DailyRecord(
    val dayOfWeek: DayOfWeek,
    val distanceKm: Double?,
    val hasRun: Boolean
)

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
