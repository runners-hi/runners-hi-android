package com.runnersHi.data.region

import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.repository.RegionRepository
import javax.inject.Inject

class RegionRepositoryImpl @Inject constructor(
    private val remoteDataSource: RegionRemoteDataSource
) : RegionRepository {

    override suspend fun searchRegions(query: String): Result<List<Region>> {
        return runCatching {
            remoteDataSource.searchRegions(query)
        }
    }

    override suspend fun selectRegion(regionId: String, regionName: String): Result<Unit> {
        return runCatching {
            remoteDataSource.selectRegion(regionId, regionName)
        }
    }
}
