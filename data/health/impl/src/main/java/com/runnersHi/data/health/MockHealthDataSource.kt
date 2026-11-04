package com.runnersHi.data.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.ExerciseType
import com.runnersHi.domain.health.model.HealthPermissionStatus
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * 디버그 빌드용 러닝 기록 (iOS MockRunningRepository와 같은 값).
 * Health Connect 데이터 없이도 홈/기록 화면을 확인할 수 있다.
 */
class MockHealthDataSource(
    private val clock: () -> LocalDate = { LocalDate.now() },
    private val zone: ZoneId = ZoneId.systemDefault()
) : HealthDataSource {

    private data class Sample(
        val daysAgo: Long,
        val meters: Double,
        val minutes: Long,
        val calories: Double
    )

    private val samples = listOf(
        Sample(0, 7_400.0, 40, 480.0),
        Sample(1, 5_000.0, 30, 320.0),
        Sample(3, 7_500.0, 45, 485.0),
        Sample(5, 10_000.0, 60, 650.0),
        Sample(7, 4_000.0, 25, 260.0),
        Sample(10, 15_000.0, 90, 980.0),
        Sample(14, 6_000.0, 35, 390.0)
    )

    override suspend fun checkPermissionStatus(): HealthPermissionStatus = HealthPermissionStatus(
        isAvailable = true,
        hasReadPermission = true,
        needsPermissionRequest = false
    )

    override suspend fun getExerciseRecords(
        startTime: Instant,
        endTime: Instant
    ): List<ExerciseRecord> {
        val today = clock()
        return samples.map { sample ->
            // 자정 직후에도 오늘 기록이 범위에서 빠지지 않도록 그날 00:00에 시작한 것으로 둔다.
            val start = today.minusDays(sample.daysAgo)
                .atStartOfDay(zone)
                .toInstant()
            ExerciseRecord(
                id = "mock_${sample.daysAgo}",
                startTime = start,
                endTime = start.plus(Duration.ofMinutes(sample.minutes)),
                exerciseType = ExerciseType.RUNNING,
                distanceMeters = sample.meters,
                durationMillis = Duration.ofMinutes(sample.minutes).toMillis(),
                calories = sample.calories
            )
        }.filter { !it.startTime.isBefore(startTime) && it.startTime.isBefore(endTime) }
    }
}
