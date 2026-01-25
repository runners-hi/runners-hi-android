package com.runnersHi.domain.ranking.usecase

import com.runnersHi.domain.ranking.model.RankingItem
import kotlinx.coroutines.flow.Flow

interface GetWeeklyRankingUseCase {
    operator fun invoke(): Flow<List<RankingItem>>
}
