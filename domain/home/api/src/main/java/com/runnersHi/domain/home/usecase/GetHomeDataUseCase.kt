package com.runnersHi.domain.home.usecase

import com.runnersHi.domain.home.model.HomeData

interface GetHomeDataUseCase {
    suspend operator fun invoke(userToken: String): Result<HomeData>
}
