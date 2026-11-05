package com.runnersHi.data.region

import com.runnersHi.core.network.DistrictResponse
import com.runnersHi.core.network.RegionResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteRegionMappingTest {

    @Test
    fun `구군이 있는 지역은 시도와 구군 이름을 합친다`() {
        val region = toRegion(RegionResponse(1, "서울특별시"), DistrictResponse(86, 1, "강남구"))

        assertEquals("서울특별시 강남구", region.name)
        assertEquals(1L to 86L, parseRegionId(region.id))
    }

    @Test
    fun `기본 구역만 있는 지역은 이름을 반복하지 않는다`() {
        val region = toRegion(RegionResponse(39, "강릉시"), DistrictResponse(39, 39, "강릉시"))

        assertEquals("강릉시", region.name)
    }

    @Test
    fun `구군 없는 아이디도 읽는다`() {
        assertEquals(39L to null, parseRegionId("39"))
    }
}
