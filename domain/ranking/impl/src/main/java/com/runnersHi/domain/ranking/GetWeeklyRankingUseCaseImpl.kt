package com.runnersHi.domain.ranking

import com.runnersHi.domain.ranking.model.RankingItem
import com.runnersHi.domain.ranking.repository.RankingRepository
import com.runnersHi.domain.ranking.usecase.GetWeeklyRankingUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyRankingUseCaseImpl @Inject constructor(
    private val rankingRepository: RankingRepository
) : GetWeeklyRankingUseCase {

    override fun invoke(): Flow<List<RankingItem>> {
        return rankingRepository.getWeeklyRanking()
    }
}
