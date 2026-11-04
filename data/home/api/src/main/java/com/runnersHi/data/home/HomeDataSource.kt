package com.runnersHi.data.home

import com.runnersHi.domain.home.model.HomeData

interface HomeDataSource {
    suspend fun getHomeData(userToken: String): HomeData
}
