package com.runnersHi.data.mission

import com.runnersHi.domain.mission.model.MissionAccent
import com.runnersHi.domain.mission.model.MissionBoard
import com.runnersHi.domain.mission.model.MissionEvent
import com.runnersHi.domain.mission.model.MissionSection
import com.runnersHi.domain.mission.model.SeasonBanner
import kotlinx.coroutines.delay
import java.time.LocalDate
import javax.inject.Inject

/** iOS MissionBoard.mock과 같은 값 */
class MockMissionDataSource @Inject constructor() : MissionRemoteDataSource {

    override suspend fun fetchMissionBoard(): MissionBoard {
        delay(300)
        return MissionBoard(listOf(welcome(), moonlight()))
    }

    private fun welcome() = MissionSection(
        id = "welcome",
        title = "Welcome Mission",
        seasonBanner = null,
        events = listOf(
            MissionEvent("w1", "데뷔전", "첫 러닝 등록", "mission_debut", true, MissionAccent.PRIMARY),
            MissionEvent("w2", "집 앞 스타트", "지역 인증", "mission_region", false, MissionAccent.PRIMARY)
        )
    )

    private fun moonlight() = MissionSection(
        id = "moonlight",
        title = "보름달 챌린지",
        seasonBanner = SeasonBanner(
            title = "추석 이벤트",
            startDate = LocalDate.of(2025, 10, 1),
            endDate = LocalDate.of(2025, 10, 31)
        ),
        events = listOf(
            MissionEvent("m1", "문라이트", "10월 러닝 인증", "mission_moonlight", false, MissionAccent.AMBER),
            MissionEvent("m2", "전력질주", "페이스 5'00\"", "mission_sprint", true, MissionAccent.AMBER),
            MissionEvent("m3", "밤톨 러닝", "1km", "mission_acorn", false, MissionAccent.AMBER),
            MissionEvent("m4", "한가위", "5일~8일 러닝 인증", "mission_hangawi", false, MissionAccent.AMBER),
            MissionEvent("m5", "송편 파워", "7일 인증", "mission_songpyeon", true, MissionAccent.AMBER),
            MissionEvent("m6", "갓러닝", "누적 거리 35km", "mission_god", false, MissionAccent.AMBER)
        )
    )
}
