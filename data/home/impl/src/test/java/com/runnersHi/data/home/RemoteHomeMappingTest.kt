package com.runnersHi.data.home

import com.runnersHi.core.network.MyRankingResponse
import com.runnersHi.domain.home.model.Tier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteHomeMappingTest {

    @Test
    fun `서버가 준 레벨과 진행률을 티어 정보에 담는다`() {
        val info = MyRankingResponse(3, "나", "SILVER", 5000, level = 7, progressPercent = 50).toTierInfo()

        assertEquals(Tier.SILVER, info.tier)
        assertEquals("Silver Runner", info.tierName)
        assertEquals(7, info.level)
        assertEquals(50, info.progressPercent)
    }

    @Test
    fun `레벨이 없는 서버 응답이면 레벨과 진행률을 비운다`() {
        val info = MyRankingResponse(3, "나", "GOLD", 5000).toTierInfo()

        assertEquals(Tier.GOLD, info.tier)
        assertNull(info.level)
        assertNull(info.progressPercent)
    }

    @Test
    fun `지역 미선택으로 순위가 없으면 브론즈로 보여 준다`() {
        val info = (null as MyRankingResponse?).toTierInfo()

        assertEquals(Tier.BRONZE, info.tier)
        assertNull(info.level)
    }
}
