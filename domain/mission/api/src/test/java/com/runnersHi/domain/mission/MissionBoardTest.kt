package com.runnersHi.domain.mission

import com.runnersHi.domain.mission.model.MissionAccent
import com.runnersHi.domain.mission.model.MissionBoard
import com.runnersHi.domain.mission.model.MissionEvent
import com.runnersHi.domain.mission.model.MissionSection
import com.runnersHi.domain.mission.model.SeasonBanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class MissionBoardTest {

    private fun event(id: String, done: Boolean) =
        MissionEvent(id, id, "", "icon", done, MissionAccent.PRIMARY)

    private val season = MissionSection(
        id = "season",
        title = "시즌",
        seasonBanner = SeasonBanner("추석", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 31)),
        events = listOf(event("s1", false), event("s2", true))
    )
    private val welcome = MissionSection("welcome", "웰컴", null, listOf(event("w1", true)))

    @Test
    fun `시즌 기간에는 시즌 섹션을 보여준다`() {
        val section = MissionBoard(listOf(welcome, season)).homeSection(LocalDate.of(2025, 10, 31))

        assertEquals("season", section?.id)
    }

    @Test
    fun `시즌이 끝나면 달성한 미션만 모아 보여준다`() {
        val section = MissionBoard(listOf(welcome, season)).homeSection(LocalDate.of(2025, 11, 1))

        assertEquals("달성한 미션", section?.title)
        assertEquals(listOf("w1", "s2"), section?.events?.map { it.id })
    }

    @Test
    fun `시즌도 달성한 미션도 없으면 보여줄 섹션이 없다`() {
        val empty = MissionSection("welcome", "웰컴", null, listOf(event("w1", false)))

        assertNull(MissionBoard(listOf(empty)).homeSection(LocalDate.of(2026, 1, 1)))
    }
}
