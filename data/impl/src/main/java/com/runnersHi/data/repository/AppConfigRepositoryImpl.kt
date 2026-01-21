package com.runnersHi.data.repository

import com.runnersHi.data.datasource.AppConfigRemoteDataSource
import com.runnersHi.domain.model.AppConfig
import com.runnersHi.domain.repository.AppConfigRepository
import javax.inject.Inject

class AppConfigRepositoryImpl @Inject constructor(
    private val remoteDataSource: AppConfigRemoteDataSource
) : AppConfigRepository {

    override suspend fun getAppConfig(): AppConfig {
        return remoteDataSource.fetchAppConfig()
    }
}
