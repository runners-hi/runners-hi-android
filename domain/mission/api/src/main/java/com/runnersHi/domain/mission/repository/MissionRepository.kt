package com.runnersHi.domain.mission.repository

import com.runnersHi.domain.mission.model.MissionBoard

interface MissionRepository {
    suspend fun getMissionBoard(): Result<MissionBoard>
}
