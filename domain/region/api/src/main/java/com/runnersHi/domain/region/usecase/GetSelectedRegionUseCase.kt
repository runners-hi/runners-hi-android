package com.runnersHi.domain.region.usecase

import com.runnersHi.domain.region.model.Region
import kotlinx.coroutines.flow.Flow

/**
 * 선택한 지역 조회 UseCase (변경되면 다시 전달)
 */
interface GetSelectedRegionUseCase {
    operator fun invoke(): Flow<Region?>
}
