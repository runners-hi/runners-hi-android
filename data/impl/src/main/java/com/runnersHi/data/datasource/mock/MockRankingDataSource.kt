package com.runnersHi.data.datasource.mock

import com.runnersHi.data.datasource.RankingLocalDataSource
import com.runnersHi.data.datasource.RankingRemoteDataSource
import com.runnersHi.domain.model.RankChange
import com.runnersHi.domain.model.RankingItem
import com.runnersHi.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockRankingLocalDataSource @Inject constructor() : RankingLocalDataSource {

    private val weeklyRanking = MutableStateFlow(createMockRanking())
    private val monthlyRanking = MutableStateFlow(createMockRanking())

    override fun getWeeklyRanking(): Flow<List<RankingItem>> = weeklyRanking

    override fun getMonthlyRanking(): Flow<List<RankingItem>> = monthlyRanking

    override suspend fun saveWeeklyRanking(ranking: List<RankingItem>) {
        weeklyRanking.value = ranking
    }

    override suspend fun saveMonthlyRanking(ranking: List<RankingItem>) {
        monthlyRanking.value = ranking
    }

    private fun createMockRanking(): List<RankingItem> = listOf(
        RankingItem(1, User("1", "김러너", null, 156.5, 42), 156.5, RankChange.NONE),
        RankingItem(2, User("2", "박조깅", null, 142.3, 38), 142.3, RankChange.UP),
        RankingItem(3, User("3", "이마라톤", null, 128.7, 35), 128.7, RankChange.DOWN),
        RankingItem(4, User("4", "최달리기", null, 115.2, 30), 115.2, RankChange.UP),
        RankingItem(5, User("5", "정스프린트", null, 98.4, 28), 98.4, RankChange.NONE)
    )
}

@Singleton
class MockRankingRemoteDataSource @Inject constructor() : RankingRemoteDataSource {

    override suspend fun fetchWeeklyRanking(): List<RankingItem> {
        return listOf(
            RankingItem(1, User("1", "김러너", null, 156.5, 42), 156.5, RankChange.NONE),
            RankingItem(2, User("2", "박조깅", null, 142.3, 38), 142.3, RankChange.UP),
            RankingItem(3, User("3", "이마라톤", null, 128.7, 35), 128.7, RankChange.DOWN),
            RankingItem(4, User("4", "최달리기", null, 115.2, 30), 115.2, RankChange.UP),
            RankingItem(5, User("5", "정스프린트", null, 98.4, 28), 98.4, RankChange.NONE)
        )
    }

    override suspend fun fetchMonthlyRanking(): List<RankingItem> {
        return fetchWeeklyRanking()
    }
}
