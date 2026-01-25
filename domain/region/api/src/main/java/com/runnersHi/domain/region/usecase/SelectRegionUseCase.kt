package com.runnersHi.domain.region.usecase

/**
 * 지역 선택 UseCase
 */
interface SelectRegionUseCase {
    suspend operator fun invoke(regionId: String, regionName: String): Result<Unit>
}
