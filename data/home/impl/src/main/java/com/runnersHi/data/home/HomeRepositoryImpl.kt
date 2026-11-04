package com.runnersHi.data.home

import com.runnersHi.domain.home.model.HomeData
import com.runnersHi.domain.home.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val dataSource: HomeDataSource
) : HomeRepository {

    override suspend fun getHomeData(userToken: String): Result<HomeData> {
        return try {
            val data = dataSource.getHomeData(userToken)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
