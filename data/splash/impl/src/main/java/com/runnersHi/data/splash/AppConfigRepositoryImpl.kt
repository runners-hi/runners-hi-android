package com.runnersHi.data.splash

import com.runnersHi.domain.splash.model.AppConfig
import com.runnersHi.domain.splash.repository.AppConfigRepository
import javax.inject.Inject

class AppConfigRepositoryImpl @Inject constructor(
    private val remoteDataSource: AppConfigRemoteDataSource
) : AppConfigRepository {

    override suspend fun getAppConfig(): AppConfig {
        return remoteDataSource.fetchAppConfig()
    }
}
