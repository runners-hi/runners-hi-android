package com.runnersHi.domain.region.repository

import com.runnersHi.domain.region.model.Region

/**
 * 지역 Repository 인터페이스
 */
interface RegionRepository {
    /**
     * 검색어로 지역 검색
     */
    suspend fun searchRegions(query: String): Result<List<Region>>

    /**
     * 지역 선택
     */
    suspend fun selectRegion(regionId: String, regionName: String): Result<Unit>
}
