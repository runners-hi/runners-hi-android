package com.runnersHi.data.ranking

import com.runnersHi.core.network.DistrictResponse
import com.runnersHi.core.network.MyProfileResponse
import com.runnersHi.core.network.MyRankingResponse
import com.runnersHi.core.network.RankingResponse
import com.runnersHi.domain.home.model.Tier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteRankingMappingTest {

    @Test
    fun `미터를 킬로미터로 바꾸고 내 항목을 표시한다`() {
        val board = toRankingBoard(
            rankings = listOf(
                RankingResponse(1, 7, "상위", "GOLD", 5300),
                RankingResponse(2, 9, "나", "SILVER", 1670)
            ),
            mine = MyRankingResponse(2, "나", "SILVER", 1670),
            profile = MyProfileResponse("나", district = DistrictResponse(86, 1, "강남구"))
        )

        assertEquals("강남구", board.regionName)
        assertEquals(5.3, board.entries[0].distanceKm, 0.001)
        assertFalse(board.entries[0].isMe)
        assertTrue(board.entries[1].isMe)
        assertEquals(Tier.SILVER, board.myEntry?.tier)
    }

    @Test
    fun `서버가 준 프로필 사진 주소를 그대로 넘긴다`() {
        val board = toRankingBoard(
            rankings = listOf(
                RankingResponse(1, 7, "상위", "GOLD", 5300, "https://img.test/top.png"),
                RankingResponse(2, 9, "사진없음", "SILVER", 1670)
            ),
            mine = MyRankingResponse(3, "나", "SILVER", 100, "https://img.test/me.png"),
            profile = MyProfileResponse("나", district = DistrictResponse(86, 1, "강남구"))
        )

        assertEquals("https://img.test/top.png", board.entries[0].profileImageUrl)
        assertEquals(null, board.entries[1].profileImageUrl)
        assertEquals("https://img.test/me.png", board.myEntry?.profileImageUrl)
    }
}
