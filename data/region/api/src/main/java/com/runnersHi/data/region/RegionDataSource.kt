package com.runnersHi.data.region

import com.runnersHi.domain.region.model.Region
import kotlinx.coroutines.flow.Flow

/**
 * 지역 데이터 소스 인터페이스
 */
interface RegionRemoteDataSource {
    /**
     * 검색어로 지역 검색
     */
    suspend fun searchRegions(query: String): List<Region>

    /**
     * 지역 선택
     */
    suspend fun selectRegion(regionId: String, regionName: String)

    /**
     * 서버에 저장된 내 지역. 목업은 null
     */
    suspend fun fetchMyRegion(): Region? = null
}

/**
 * 선택한 지역 로컬 저장소
 */
interface RegionLocalDataSource {
    fun observeSelectedRegion(): Flow<Region?>
    suspend fun saveSelectedRegion(region: Region)
    suspend fun clearSelectedRegion()
}
