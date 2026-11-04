package com.runnersHi.data.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.HealthPermissionStatus
import com.runnersHi.domain.health.repository.HealthRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val healthDataSource: HealthDataSource
) : HealthRepository {

    override suspend fun checkPermissionStatus(): HealthPermissionStatus {
        return healthDataSource.checkPermissionStatus()
    }

    override suspend fun getExerciseRecords(
        startTime: Instant,
        endTime: Instant
    ): Result<List<ExerciseRecord>> {
        return try {
            val records = healthDataSource.getExerciseRecords(startTime, endTime)
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTodayRunningRecords(): Result<List<ExerciseRecord>> {
        return try {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val startOfDay = today.atStartOfDay(zone).toInstant()
            val endOfDay = today.plusDays(1).atStartOfDay(zone).toInstant()

            val records = healthDataSource.getExerciseRecords(startOfDay, endOfDay)
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeekRunningRecords(): Result<List<ExerciseRecord>> {
        return try {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                .atStartOfDay(zone).toInstant()
            val endOfWeek = today.plusDays(1).atStartOfDay(zone).toInstant()

            val records = healthDataSource.getExerciseRecords(startOfWeek, endOfWeek)
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
