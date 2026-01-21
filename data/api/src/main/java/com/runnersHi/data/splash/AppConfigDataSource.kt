package com.runnersHi.data.splash

import com.runnersHi.domain.splash.model.AppConfig

interface AppConfigRemoteDataSource {
    suspend fun fetchAppConfig(): AppConfig
}
