package com.runnersHi.presentation.model

import androidx.compose.runtime.Immutable
import com.runnersHi.domain.model.RankChange
import com.runnersHi.domain.model.RankingItem
import com.runnersHi.domain.model.User

@Immutable
data class UserUiModel(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val totalDistance: Double,
    val totalRuns: Int,
    val rank: Int? = null
)

@Immutable
data class RankingItemUiModel(
    val rank: Int,
    val user: UserUiModel,
    val score: Double,
    val change: RankChangeUiModel = RankChangeUiModel.NONE
)

enum class RankChangeUiModel {
    UP, DOWN, NONE
}

fun User.toUiModel(): UserUiModel = UserUiModel(
    id = id,
    name = name,
    profileImageUrl = profileImageUrl,
    totalDistance = totalDistance,
    totalRuns = totalRuns,
    rank = rank
)

fun RankingItem.toUiModel(): RankingItemUiModel = RankingItemUiModel(
    rank = rank,
    user = user.toUiModel(),
    score = score,
    change = change.toUiModel()
)

fun RankChange.toUiModel(): RankChangeUiModel = when (this) {
    RankChange.UP -> RankChangeUiModel.UP
    RankChange.DOWN -> RankChangeUiModel.DOWN
    RankChange.NONE -> RankChangeUiModel.NONE
}
