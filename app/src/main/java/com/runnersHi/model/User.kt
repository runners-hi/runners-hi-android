package com.runnersHi.model

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val totalDistance: Double,
    val totalRuns: Int,
    val rank: Int? = null
)

@Immutable
data class RunRecord(
    val id: String,
    val userId: String,
    val distance: Double,
    val duration: Long,
    val pace: Double,
    val date: Long,
    val calories: Int
)

@Immutable
data class RankingItem(
    val rank: Int,
    val user: User,
    val score: Double,
    val change: RankChange = RankChange.NONE
)

enum class RankChange {
    UP, DOWN, NONE
}
