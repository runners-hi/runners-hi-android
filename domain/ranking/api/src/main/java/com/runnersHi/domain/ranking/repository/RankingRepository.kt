package com.runnersHi.domain.ranking.repository

import com.runnersHi.domain.ranking.model.RankingItem
import kotlinx.coroutines.flow.Flow

interface RankingRepository {
    fun getWeeklyRanking(): Flow<List<RankingItem>>
    fun getMonthlyRanking(): Flow<List<RankingItem>>
    suspend fun refreshRanking()
}
