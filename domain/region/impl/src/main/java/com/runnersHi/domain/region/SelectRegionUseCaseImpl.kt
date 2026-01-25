package com.runnersHi.domain.region

import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.region.usecase.SelectRegionUseCase
import javax.inject.Inject

class SelectRegionUseCaseImpl @Inject constructor(
    private val repository: RegionRepository
) : SelectRegionUseCase {
    override suspend fun invoke(regionId: String, regionName: String): Result<Unit> {
        return repository.selectRegion(regionId, regionName)
    }
}
