package com.runnersHi.data.datasource

import com.runnersHi.domain.model.AppConfig

interface AppConfigRemoteDataSource {
    suspend fun fetchAppConfig(): AppConfig
}
