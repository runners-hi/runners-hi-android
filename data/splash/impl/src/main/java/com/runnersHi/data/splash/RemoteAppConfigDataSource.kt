package com.runnersHi.data.splash

import com.runnersHi.core.network.AppVersionResponse
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.domain.splash.model.AppConfig

class RemoteAppConfigDataSource(
    private val api: RunnersHiApi,
    private val currentVersion: String
) : AppConfigRemoteDataSource {

    override suspend fun fetchAppConfig(): AppConfig =
        apiCall { api.checkVersion(PLATFORM, currentVersion) }.toAppConfig(currentVersion)

    private companion object {
        const val PLATFORM = "ANDROID"
    }
}

/** 서버는 최소 버전 대신 강제 업데이트 여부만 준다. */
internal fun AppVersionResponse.toAppConfig(currentVersion: String) = AppConfig(
    minVersion = if (forceUpdate) latestVersion else currentVersion,
    latestVersion = latestVersion,
    maintenanceMode = false,
    maintenanceMessage = ""
)
