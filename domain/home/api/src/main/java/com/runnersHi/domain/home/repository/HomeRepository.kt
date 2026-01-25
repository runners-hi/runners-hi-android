package com.runnersHi.domain.home.repository

import com.runnersHi.domain.home.model.HomeData

interface HomeRepository {
    suspend fun getHomeData(userToken: String): Result<HomeData>
}
