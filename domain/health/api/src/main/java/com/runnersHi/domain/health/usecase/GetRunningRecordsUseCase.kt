package com.runnersHi.domain.health.usecase

import com.runnersHi.domain.health.model.ExerciseRecord

/**
 * 최근 러닝 기록 조회 (최신순)
 */
interface GetRunningRecordsUseCase {
    suspend operator fun invoke(): Result<List<ExerciseRecord>>
}
