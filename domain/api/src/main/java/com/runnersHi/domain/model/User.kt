package com.runnersHi.domain.model

data class User(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val totalDistance: Double,
    val totalRuns: Int,
    val rank: Int? = null
)
