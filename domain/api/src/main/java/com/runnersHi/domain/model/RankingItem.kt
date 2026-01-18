package com.runnersHi.domain.model

data class RankingItem(
    val rank: Int,
    val user: User,
    val score: Double,
    val change: RankChange = RankChange.NONE
)

enum class RankChange {
    UP, DOWN, NONE
}
