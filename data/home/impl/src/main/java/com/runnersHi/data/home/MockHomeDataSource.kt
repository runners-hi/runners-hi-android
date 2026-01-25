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
                    MissionItem("1", "문라이트", "10월 러닝 인증", "", true),
                    MissionItem("2", "전력질주", "페이스 5'00\"", "", false),
                    MissionItem("3", "밤톨 러닝", "1km", "", true),
                    MissionItem("4", "한가위", "10월 5일-10월 8일 러닝 인증", "", false),
                    MissionItem("5", "송편 파워", "10월 6일 인증", "", false),
                    MissionItem("6", "갓러닝", "누적 거리 35km", "", false)
                )
            )
        )
    }
}
