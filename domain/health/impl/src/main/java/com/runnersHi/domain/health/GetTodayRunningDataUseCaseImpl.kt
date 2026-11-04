package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.usecase.GetTodayRunningDataUseCase
import javax.inject.Inject

class GetTodayRunningDataUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository
) : GetTodayRunningDataUseCase {
    override suspend fun invoke(): Result<List<ExerciseRecord>> {
        return healthRepository.getTodayRunningRecords()
    }
}
