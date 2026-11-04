package com.runnersHi.domain.region

import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelectedRegionUseCaseImpl @Inject constructor(
    private val regionRepository: RegionRepository
) : GetSelectedRegionUseCase {

    override fun invoke(): Flow<Region?> = regionRepository.observeSelectedRegion()
}
