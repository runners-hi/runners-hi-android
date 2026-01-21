package com.runnersHi.data.repository

import com.runnersHi.data.datasource.RankingLocalDataSource
import com.runnersHi.data.datasource.RankingRemoteDataSource
import com.runnersHi.domain.model.RankingItem
import com.runnersHi.domain.repository.RankingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(
    private val localDataSource: RankingLocalDataSource,
    private val remoteDataSource: RankingRemoteDataSource
) : RankingRepository {

    override fun getWeeklyRanking(): Flow<List<RankingItem>> {
        return localDataSource.getWeeklyRanking()
    }

    override fun getMonthlyRanking(): Flow<List<RankingItem>> {
        return localDataSource.getMonthlyRanking()
    }

    override suspend fun refreshRanking() {
        val weeklyRanking = remoteDataSource.fetchWeeklyRanking()
        localDataSource.saveWeeklyRanking(weeklyRanking)

        val monthlyRanking = remoteDataSource.fetchMonthlyRanking()
        localDataSource.saveMonthlyRanking(monthlyRanking)
    }
}
