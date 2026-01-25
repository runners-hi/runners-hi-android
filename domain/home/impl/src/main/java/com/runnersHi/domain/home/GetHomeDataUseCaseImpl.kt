package com.runnersHi.domain.home

import com.runnersHi.domain.home.model.HomeData
import com.runnersHi.domain.home.repository.HomeRepository
import com.runnersHi.domain.home.usecase.GetHomeDataUseCase
import javax.inject.Inject

class GetHomeDataUseCaseImpl @Inject constructor(
    private val repository: HomeRepository
) : GetHomeDataUseCase {
    override suspend fun invoke(userToken: String): Result<HomeData> {
        return repository.getHomeData(userToken)
    }
}
