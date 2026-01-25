package com.runnersHi.domain.region

import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.region.usecase.SearchRegionsUseCase
import javax.inject.Inject

class SearchRegionsUseCaseImpl @Inject constructor(
    private val repository: RegionRepository
) : SearchRegionsUseCase {
    override suspend fun invoke(query: String): Result<List<Region>> {
        return repository.searchRegions(query)
    }
}
