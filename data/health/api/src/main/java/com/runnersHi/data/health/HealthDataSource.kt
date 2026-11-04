package com.runnersHi.data.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.model.HealthPermissionStatus
import java.time.Instant

interface HealthDataSource {
    suspend fun checkPermissionStatus(): HealthPermissionStatus
    suspend fun getExerciseRecords(startTime: Instant, endTime: Instant): List<ExerciseRecord>
}
