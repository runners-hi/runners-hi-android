package com.runnersHi.data.mission

import com.runnersHi.core.network.MissionGroupResponse
import com.runnersHi.core.network.MissionResponse
import com.runnersHi.domain.mission.model.MissionAccent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RemoteMissionMappingTest {

    private val missions = listOf(
        MissionResponse(1, "데뷔전", "첫 러닝 등록", "https://img/1.png", "ACHIEVED"),
        MissionResponse(2, "문라이트", null, null, "IN_PROGRESS")
    )

    @Test
    fun `이벤트 그룹은 기간 배너와 강조색을 갖는다`() {
        val section = MissionGroupResponse(5, "보름달", "추석", "EVENT", "2026-10-01", "2026-10-31", missions).toSection()

        assertEquals(LocalDate.of(2026, 10, 1), section.seasonBanner?.startDate)
        assertEquals(MissionAccent.AMBER, section.events[0].accent)
    }

    @Test
    fun `상시 그룹은 배너가 없고 달성 상태만 완료로 본다`() {
        val section = MissionGroupResponse(1, "웰컴", null, "WELCOME", missions = missions).toSection()

        assertNull(section.seasonBanner)
        assertEquals(MissionAccent.PRIMARY, section.events[0].accent)
        assertTrue(section.events[0].isCompleted)
        assertFalse(section.events[1].isCompleted)
        assertEquals("https://img/1.png", section.events[0].imageUrl)
    }
}
