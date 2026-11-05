package com.runnersHi.data.mission

import com.runnersHi.core.network.MissionGroupResponse
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.domain.mission.model.MissionAccent
import com.runnersHi.domain.mission.model.MissionBoard
import com.runnersHi.domain.mission.model.MissionEvent
import com.runnersHi.domain.mission.model.MissionSection
import com.runnersHi.domain.mission.model.SeasonBanner
import java.time.LocalDate
import javax.inject.Inject

class RemoteMissionDataSource @Inject constructor(
    private val api: RunnersHiApi
) : MissionRemoteDataSource {

    override suspend fun fetchMissionBoard(): MissionBoard =
        MissionBoard(apiCall { api.getMissionGroups() }.map { it.toSection() })
}

internal fun MissionGroupResponse.toSection(): MissionSection {
    val isEvent = type == "EVENT"
    val start = startDate?.let(LocalDate::parse)
    val end = endDate?.let(LocalDate::parse)
    return MissionSection(
        id = id.toString(),
        title = name,
        seasonBanner = if (isEvent && start != null && end != null) {
            SeasonBanner(title = description ?: name, startDate = start, endDate = end)
        } else {
            null
        },
        events = missions.map { mission ->
            MissionEvent(
                id = mission.id.toString(),
                title = mission.name,
                subtitle = mission.description.orEmpty(),
                iconName = "",
                isCompleted = mission.status == "ACHIEVED",
                accent = if (isEvent) MissionAccent.AMBER else MissionAccent.PRIMARY,
                imageUrl = mission.imageUrl
            )
        }
    )
}
