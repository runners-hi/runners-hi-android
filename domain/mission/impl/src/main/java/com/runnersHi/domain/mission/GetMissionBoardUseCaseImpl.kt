package com.runnersHi.domain.mission

import com.runnersHi.domain.mission.model.MissionBoard
import com.runnersHi.domain.mission.repository.MissionRepository
import com.runnersHi.domain.mission.usecase.GetMissionBoardUseCase
import javax.inject.Inject

class GetMissionBoardUseCaseImpl @Inject constructor(
    private val missionRepository: MissionRepository
) : GetMissionBoardUseCase {

    override suspend fun invoke(): Result<MissionBoard> = missionRepository.getMissionBoard()
}
