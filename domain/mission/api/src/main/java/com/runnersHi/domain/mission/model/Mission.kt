package com.runnersHi.domain.mission.model

import java.time.LocalDate

enum class MissionAccent { PRIMARY, AMBER }

data class MissionEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val isCompleted: Boolean,
    val accent: MissionAccent
)

data class SeasonBanner(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    fun isActive(on: LocalDate): Boolean = !on.isBefore(startDate) && !on.isAfter(endDate)
}

data class MissionSection(
    val id: String,
    val title: String,
    val seasonBanner: SeasonBanner?,
    val events: List<MissionEvent>
)

data class MissionBoard(
    val sections: List<MissionSection>
) {
    /** 진행 중인 시즌이 있으면 그 시즌을, 없으면 달성한 미션을 홈에 보여준다. */
    fun homeSection(on: LocalDate): MissionSection? {
        sections.firstOrNull { it.seasonBanner?.isActive(on) == true }?.let { return it }

        val completed = sections.flatMap { it.events }.filter { it.isCompleted }
        if (completed.isEmpty()) return null
        return MissionSection(
            id = "home_completed",
            title = "달성한 미션",
            seasonBanner = null,
            events = completed
        )
    }
}
