package com.runnersHi.data.ranking

import com.runnersHi.domain.ranking.model.RankingBoard

interface RankingRemoteDataSource {
    suspend fun fetchRankingBoard(): RankingBoard
}
