package com.runnersHi.domain.repository

import com.runnersHi.domain.model.AppConfig

interface AppConfigRepository {
    suspend fun getAppConfig(): AppConfig
}
