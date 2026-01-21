package com.runnersHi.domain.user.model

data class RunRecord(
    val id: String,
    val userId: String,
    val distance: Double,
    val duration: Long,
    val pace: Double,
    val date: Long,
    val calories: Int
)
