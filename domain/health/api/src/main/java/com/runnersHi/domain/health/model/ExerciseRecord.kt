package com.runnersHi.domain.health.model

import java.time.Instant

data class ExerciseRecord(
    val id: String,
    val startTime: Instant,
    val endTime: Instant,
    val exerciseType: ExerciseType,
    val distanceMeters: Double?,
    val durationMillis: Long,
    val calories: Double?
)

enum class ExerciseType {
    RUNNING,
    WALKING,
    CYCLING,
    OTHER
}
