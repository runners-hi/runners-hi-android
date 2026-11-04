package com.runnersHi.domain.ranking

import com.runnersHi.domain.ranking.model.RankingBoard
import com.runnersHi.domain.ranking.repository.RankingRepository
import com.runnersHi.domain.ranking.usecase.GetRankingBoardUseCase
import javax.inject.Inject

class GetRankingBoardUseCaseImpl @Inject constructor(
    private val rankingRepository: RankingRepository
) : GetRankingBoardUseCase {

    override suspend fun invoke(): Result<RankingBoard> = rankingRepository.getRankingBoard()
}
