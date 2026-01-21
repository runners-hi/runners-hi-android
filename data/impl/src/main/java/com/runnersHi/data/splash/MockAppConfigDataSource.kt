package com.runnersHi.data.splash

import com.runnersHi.domain.splash.model.AppConfig
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAppConfigRemoteDataSource @Inject constructor() : AppConfigRemoteDataSource {

    override suspend fun fetchAppConfig(): AppConfig {
        // 네트워크 지연 시뮬레이션
        delay(500)

        return AppConfig(
            minVersion = "1.0.0",
            latestVersion = "1.2.0",
            maintenanceMode = false,
            maintenanceMessage = ""
        )
    }
}
