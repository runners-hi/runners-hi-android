package com.runnersHi.data.ranking

import com.runnersHi.domain.ranking.model.RankingBoard
import com.runnersHi.domain.ranking.repository.RankingRepository
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(
    private val remoteDataSource: RankingRemoteDataSource
) : RankingRepository {

    override suspend fun getRankingBoard(): Result<RankingBoard> =
        runCatching { remoteDataSource.fetchRankingBoard() }
}
