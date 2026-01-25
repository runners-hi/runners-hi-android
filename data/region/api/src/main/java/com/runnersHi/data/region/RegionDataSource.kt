package com.runnersHi.data.region

import com.runnersHi.domain.region.model.Region

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
}
