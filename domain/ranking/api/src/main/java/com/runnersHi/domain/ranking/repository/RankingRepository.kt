package com.runnersHi.domain.ranking.repository

import com.runnersHi.domain.ranking.model.RankingBoard

interface RankingRepository {
    suspend fun getRankingBoard(): Result<RankingBoard>
}
