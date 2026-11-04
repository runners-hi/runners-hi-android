package com.runnersHi.data.mission

import com.runnersHi.domain.mission.model.MissionBoard
import com.runnersHi.domain.mission.repository.MissionRepository
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(
    private val remoteDataSource: MissionRemoteDataSource
) : MissionRepository {

    override suspend fun getMissionBoard(): Result<MissionBoard> =
        runCatching { remoteDataSource.fetchMissionBoard() }
}
