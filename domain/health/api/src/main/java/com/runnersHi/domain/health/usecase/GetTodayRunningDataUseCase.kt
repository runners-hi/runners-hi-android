package com.runnersHi.domain.health.usecase

import com.runnersHi.domain.health.model.ExerciseRecord

interface GetTodayRunningDataUseCase {
    suspend operator fun invoke(): Result<List<ExerciseRecord>>
}
