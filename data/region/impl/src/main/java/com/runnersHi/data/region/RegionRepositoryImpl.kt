package com.runnersHi.data.region

import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.repository.RegionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegionRepositoryImpl @Inject constructor(
    private val remoteDataSource: RegionRemoteDataSource,
    private val localDataSource: RegionLocalDataSource
) : RegionRepository {

    override suspend fun searchRegions(query: String): Result<List<Region>> {
        return runCatching {
            remoteDataSource.searchRegions(query)
        }
    }

    override suspend fun selectRegion(regionId: String, regionName: String): Result<Unit> {
        return runCatching {
            remoteDataSource.selectRegion(regionId, regionName)
            localDataSource.saveSelectedRegion(Region(regionId, regionName))
        }
    }

    override suspend fun syncFromServer() {
        remoteDataSource.fetchMyRegion()?.let { localDataSource.saveSelectedRegion(it) }
    }

    override fun observeSelectedRegion(): Flow<Region?> = localDataSource.observeSelectedRegion()

    override suspend fun clearSelectedRegion() {
        localDataSource.clearSelectedRegion()
    }
}
