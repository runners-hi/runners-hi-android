package com.runnersHi.data.mission

import com.runnersHi.domain.mission.model.MissionBoard

interface MissionRemoteDataSource {
    suspend fun fetchMissionBoard(): MissionBoard
}
