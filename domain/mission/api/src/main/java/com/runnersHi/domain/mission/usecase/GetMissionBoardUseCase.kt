package com.runnersHi.domain.mission.usecase

import com.runnersHi.domain.mission.model.MissionBoard

interface GetMissionBoardUseCase {
    suspend operator fun invoke(): Result<MissionBoard>
}
