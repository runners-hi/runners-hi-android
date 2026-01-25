package com.runnersHi.data.home

import com.runnersHi.domain.home.model.DailyRecord
import com.runnersHi.domain.home.model.DayOfWeek
import com.runnersHi.domain.home.model.EventBanner
import com.runnersHi.domain.home.model.HomeData
import com.runnersHi.domain.home.model.MissionEventData
import com.runnersHi.domain.home.model.MissionItem
import com.runnersHi.domain.home.model.ThisWeekData
import com.runnersHi.domain.home.model.Tier
import com.runnersHi.domain.home.model.TierInfo
import com.runnersHi.domain.home.model.TodaysRun
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockHomeDataSource @Inject constructor() : HomeDataSource {

    override suspend fun getHomeData(userToken: String): HomeData {
        delay(500) // Simulate network delay

        return HomeData(
            tier = TierInfo(
                tier = Tier.GOLD,
                tierName = "Gold Runner",
                level = 31,
                progressPercent = 10
            ),
            todaysRun = TodaysRun(
                distanceKm = 7.4,
                paceMinutes = 8,
                paceSeconds = 0,
                timeMinutes = 40,
                timeSeconds = 0
            ),
            thisWeek = ThisWeekData(
                totalDistanceKm = 7.4,
                dailyRecords = listOf(
                    DailyRecord(DayOfWeek.MONDAY, 10.0, true),
                    DailyRecord(DayOfWeek.TUESDAY, null, false),
                    DailyRecord(DayOfWeek.WEDNESDAY, 0.3, true),
                    DailyRecord(DayOfWeek.THURSDAY, 0.3, true),
                    DailyRecord(DayOfWeek.FRIDAY, 0.3, true),
                    DailyRecord(DayOfWeek.SATURDAY, 0.3, true),
                    DailyRecord(DayOfWeek.SUNDAY, 0.3, true)
                )
            ),
            missionEvent = MissionEventData(
                eventBanner = EventBanner(
                    title = "추석 이벤트",
                    period = "2025.10.01 - 2025.10.31"
                ),
                missions = listOf(
                    // Icons from Figma with proper sizes (temporary URLs - replace with server URLs)
                    MissionItem(
                        id = "1",
                        name = "문라이트",
                        description = "10월 러닝 인증",
                        imageUrl = "https://www.figma.com/api/mcp/asset/a8f8a1a9-9e24-4828-b42c-e4d3abfa0145",
                        isCompleted = true,
                        iconWidthDp = 60,
                        iconHeightDp = 60
                    ),
                    MissionItem(
                        id = "2",
                        name = "전력질주",
                        description = "페이스 5'00\"",
                        imageUrl = "https://www.figma.com/api/mcp/asset/f5e0028b-80cb-4cb3-909a-41effc29438d",
                        isCompleted = false,
                        iconWidthDp = 60,
                        iconHeightDp = 60
                    ),
                    MissionItem(
                        id = "3",
                        name = "밤톨 러닝",
                        description = "1km",
                        imageUrl = "https://www.figma.com/api/mcp/asset/7c893276-8ffd-4589-af15-c8552e0680ce",
                        isCompleted = true,
                        iconWidthDp = 60,
                        iconHeightDp = 60
                    ),
                    MissionItem(
                        id = "4",
                        name = "한가위",
                        description = "10월 5일-10월 8일 러닝 인증",
                        imageUrl = "https://www.figma.com/api/mcp/asset/31161f96-4fd0-4070-a25f-efd44de5cfc8",
                        isCompleted = false,
                        iconWidthDp = 104,
                        iconHeightDp = 104
                    ),
                    MissionItem(
                        id = "5",
                        name = "송편 파워",
                        description = "10월 6일 인증",
                        imageUrl = "https://www.figma.com/api/mcp/asset/95ed220b-e990-44f6-961a-11cfc23547b3",
                        isCompleted = false,
                        iconWidthDp = 63,
                        iconHeightDp = 63
                    ),
                    MissionItem(
                        id = "6",
                        name = "갓러닝",
                        description = "누적 거리 35km",
                        imageUrl = "https://www.figma.com/api/mcp/asset/6017abce-443a-4ad9-9ae4-41cb6f062dd6",
                        isCompleted = false,
                        iconWidthDp = 65,
                        iconHeightDp = 65
                    )
                )
            )
        )
    }
}
