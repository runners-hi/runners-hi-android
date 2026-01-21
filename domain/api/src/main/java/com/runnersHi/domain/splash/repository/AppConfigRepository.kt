package com.runnersHi.domain.splash.repository

import com.runnersHi.domain.splash.model.AppConfig

interface AppConfigRepository {
    suspend fun getAppConfig(): AppConfig
}
