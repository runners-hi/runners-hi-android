package com.runnersHi.domain.ranking.model

import com.runnersHi.domain.home.model.Tier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RankingBoardTest {

    private fun entry(rank: Int, isMe: Boolean = false) =
        RankingEntry(rank, "러너$rank", Tier.GOLD, 10.0, isMe)

    private val entries = (1..20).map { entry(it) }

    @Test
    fun `상위 5명만 보여준다`() {
        val board = RankingBoard("인천시", entries, entry(15, isMe = true))

        assertEquals(listOf(1, 2, 3, 4, 5), board.topEntries.map { it.rank })
    }

    @Test
    fun `내 순위가 6위 이상이면 상위권이 아니다`() {
        assertFalse(RankingBoard("인천시", entries, entry(6, isMe = true)).isMyEntryInTop)
        assertTrue(RankingBoard("인천시", entries, entry(5, isMe = true)).isMyEntryInTop)
    }

    @Test
    fun `내 기록이 없으면 상위권이 아니다`() {
        assertFalse(RankingBoard("인천시", entries, null).isMyEntryInTop)
    }

    @Test
    fun `내 항목에는 닉네임 뒤에 표시를 붙인다`() {
        assertEquals("러너1 (나)", entry(1, isMe = true).displayNickname)
        assertEquals("러너1", entry(1).displayNickname)
    }
}
