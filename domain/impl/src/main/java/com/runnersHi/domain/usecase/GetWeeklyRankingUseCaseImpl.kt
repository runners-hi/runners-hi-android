package com.runnersHi.domain.usecase

import com.runnersHi.domain.model.RankingItem
import com.runnersHi.domain.repository.RankingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyRankingUseCaseImpl @Inject constructor(
    private val rankingRepository: RankingRepository
) : GetWeeklyRankingUseCase {

    override fun invoke(): Flow<List<RankingItem>> {
        return rankingRepository.getWeeklyRanking()
    }
}
