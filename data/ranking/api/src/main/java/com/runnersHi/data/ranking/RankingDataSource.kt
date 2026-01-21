package com.runnersHi.data.ranking

import com.runnersHi.domain.ranking.model.RankingItem
import kotlinx.coroutines.flow.Flow

interface RankingLocalDataSource {
    fun getWeeklyRanking(): Flow<List<RankingItem>>
    fun getMonthlyRanking(): Flow<List<RankingItem>>
    suspend fun saveWeeklyRanking(ranking: List<RankingItem>)
    suspend fun saveMonthlyRanking(ranking: List<RankingItem>)
}

interface RankingRemoteDataSource {
    suspend fun fetchWeeklyRanking(): List<RankingItem>
    suspend fun fetchMonthlyRanking(): List<RankingItem>
}
