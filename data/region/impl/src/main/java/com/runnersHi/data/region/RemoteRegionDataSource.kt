package com.runnersHi.data.region

import com.runnersHi.core.network.DistrictResponse
import com.runnersHi.core.network.RegionResponse
import com.runnersHi.core.network.RegionUpdateRequest
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.core.network.apiCallNullable
import com.runnersHi.domain.region.model.Region
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 서버 지역은 시·도와 구·군 2단계다. 앱의 1단계 검색에 맞춰 "서울특별시 강남구" 목록으로 펼친다.
 * 구·군이 없는 지역은 서버가 지역과 같은 이름의 기본 구역을 준다.
 */
@Singleton
class RemoteRegionDataSource @Inject constructor(
    private val api: RunnersHiApi
) : RegionRemoteDataSource {

    private val cacheLock = Mutex()
    private var cache: List<Region>? = null

    override suspend fun searchRegions(query: String): List<Region> {
        if (query.isBlank()) return allRegions()
        return allRegions().filter { it.name.contains(query.trim()) }
    }

    override suspend fun selectRegion(regionId: String, regionName: String) {
        val (region, district) = parseRegionId(regionId)
        apiCallNullable { api.updateRegion(RegionUpdateRequest(region, district)) }
    }

    override suspend fun fetchMyRegion(): Region? {
        val profile = apiCall { api.getMyProfile() }
        val region = profile.region ?: return null
        val district = profile.district ?: return Region(region.id.toString(), region.name)
        return toRegion(region, district)
    }

    private suspend fun allRegions(): List<Region> = cacheLock.withLock {
        cache ?: fetchAll().also { cache = it }
    }

    private suspend fun fetchAll(): List<Region> = coroutineScope {
        val limit = Semaphore(MAX_CONCURRENT_REQUESTS)
        apiCall { api.getRegions() }
            .map { region ->
                async {
                    val districts = limit.withPermit { apiCall { api.getDistricts(region.id) } }
                    districts.map { toRegion(region, it) }
                }
            }
            .flatMap { it.await() }
            .sortedBy { it.name }
    }

    private companion object {
        const val MAX_CONCURRENT_REQUESTS = 8
    }
}

internal fun toRegion(region: RegionResponse, district: DistrictResponse) = Region(
    id = "${region.id}:${district.id}",
    name = if (region.name == district.name) region.name else "${region.name} ${district.name}"
)

internal fun parseRegionId(id: String): Pair<Long, Long?> {
    val parts = id.split(":")
    return parts[0].toLong() to parts.getOrNull(1)?.toLong()
}
