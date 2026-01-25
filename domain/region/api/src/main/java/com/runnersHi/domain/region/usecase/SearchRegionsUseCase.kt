package com.runnersHi.domain.region.usecase

import com.runnersHi.domain.region.model.Region

/**
 * 지역 검색 UseCase
 */
interface SearchRegionsUseCase {
    suspend operator fun invoke(query: String): Result<List<Region>>
}
