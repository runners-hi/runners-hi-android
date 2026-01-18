package com.runnersHi.domain.repository

import com.runnersHi.domain.model.RankingItem
import kotlinx.coroutines.flow.Flow

interface RankingRepository {
    fun getWeeklyRanking(): Flow<List<RankingItem>>
    fun getMonthlyRanking(): Flow<List<RankingItem>>
    suspend fun refreshRanking()
}
