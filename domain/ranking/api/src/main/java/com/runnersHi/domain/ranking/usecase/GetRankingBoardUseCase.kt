package com.runnersHi.domain.ranking.usecase

import com.runnersHi.domain.ranking.model.RankingBoard

interface GetRankingBoardUseCase {
    suspend operator fun invoke(): Result<RankingBoard>
}
