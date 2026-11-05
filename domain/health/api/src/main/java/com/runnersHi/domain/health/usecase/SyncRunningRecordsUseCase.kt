package com.runnersHi.domain.health.usecase

/**
 * 기기의 러닝 기록 중 서버에 없는 것을 올린다.
 * @return 새로 올린 기록 수
 */
interface SyncRunningRecordsUseCase {
    suspend operator fun invoke(): Result<Int>
}
