package com.runnersHi.domain.health.repository

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.HealthPermissionStatus
import java.time.Instant

interface HealthRepository {
    suspend fun checkPermissionStatus(): HealthPermissionStatus
    suspend fun getExerciseRecords(startTime: Instant, endTime: Instant): Result<List<ExerciseRecord>>
    suspend fun getTodayRunningRecords(): Result<List<ExerciseRecord>>
    suspend fun getWeekRunningRecords(): Result<List<ExerciseRecord>>
}
