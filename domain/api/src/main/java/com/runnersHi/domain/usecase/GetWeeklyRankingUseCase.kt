package com.runnersHi.domain.usecase

import com.runnersHi.domain.model.RankingItem
import kotlinx.coroutines.flow.Flow

interface GetWeeklyRankingUseCase {
    operator fun invoke(): Flow<List<RankingItem>>
}
