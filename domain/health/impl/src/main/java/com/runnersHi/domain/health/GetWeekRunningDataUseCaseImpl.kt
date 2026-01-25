package com.runnersHi.domain.health

import com.runnersHi.domain.health.model.ExerciseRecord
import com.runnersHi.domain.health.repository.HealthRepository
import com.runnersHi.domain.health.usecase.GetWeekRunningDataUseCase
import javax.inject.Inject

class GetWeekRunningDataUseCaseImpl @Inject constructor(
    private val healthRepository: HealthRepository
) : GetWeekRunningDataUseCase {
    override suspend fun invoke(): Result<List<ExerciseRecord>> {
        return healthRepository.getWeekRunningRecords()
    }
}
