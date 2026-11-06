package com.runnersHi.di

import com.runnersHi.BuildConfig
import com.runnersHi.core.network.ImageUploader
import com.runnersHi.core.network.NetworkFactory
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.ServerEndpoint
import com.runnersHi.core.network.TokenStore
import com.runnersHi.data.auth.AuthLocalDataSource
import com.runnersHi.data.auth.AuthRemoteDataSource
import com.runnersHi.data.auth.RemoteAuthDataSource
import com.runnersHi.data.health.RemoteRunningRecordRepository
import com.runnersHi.data.home.HomeDataSource
import com.runnersHi.data.home.RemoteHomeDataSource
import com.runnersHi.data.mission.MissionRemoteDataSource
import com.runnersHi.data.mission.RemoteMissionDataSource
import com.runnersHi.data.notice.NoticeRemoteDataSource
import com.runnersHi.data.notice.RemoteNoticeDataSource
import com.runnersHi.data.notification.NotificationRemoteDataSource
import com.runnersHi.data.notification.RemoteNotificationDataSource
import com.runnersHi.data.ranking.RankingRemoteDataSource
import com.runnersHi.data.ranking.RemoteRankingDataSource
import com.runnersHi.data.region.RegionRemoteDataSource
import com.runnersHi.data.region.RemoteRegionDataSource
import com.runnersHi.data.splash.AppConfigRemoteDataSource
import com.runnersHi.data.splash.RemoteAppConfigDataSource
import com.runnersHi.data.terms.RemoteTermsDataSource
import com.runnersHi.data.terms.TermsRemoteDataSource
import com.runnersHi.data.user.RemoteUserDataSource
import com.runnersHi.data.user.UserRemoteDataSource
import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.health.repository.RunningRecordRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

/** 모든 데이터는 서버에서 온다. 서버 주소는 [ServerEndpoint]가 요청마다 정한다(릴리스는 항상 상용). */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenStore(local: AuthLocalDataSource): TokenStore = AuthTokenStore(local)

    @Provides
    @Singleton
    fun provideApi(tokenStore: TokenStore): RunnersHiApi =
        NetworkFactory.createApi(
            baseUrl = ServerEndpoint.PROD_URL,
            tokenStore = tokenStore,
            endpoint = { ServerEndpoint.baseUrl }
        )

    @Provides
    @Singleton
    fun provideAppConfig(api: RunnersHiApi): AppConfigRemoteDataSource =
        RemoteAppConfigDataSource(api, BuildConfig.VERSION_NAME)

    @Provides
    @Singleton
    fun provideImageUploader(): ImageUploader = ImageUploader()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteBindingsModule {
    @Binds @Singleton abstract fun auth(impl: RemoteAuthDataSource): AuthRemoteDataSource
    @Binds @Singleton abstract fun user(impl: RemoteUserDataSource): UserRemoteDataSource
    @Binds @Singleton abstract fun ranking(impl: RemoteRankingDataSource): RankingRemoteDataSource
    @Binds @Singleton abstract fun terms(impl: RemoteTermsDataSource): TermsRemoteDataSource
    @Binds @Singleton abstract fun region(impl: RemoteRegionDataSource): RegionRemoteDataSource
    @Binds @Singleton abstract fun mission(impl: RemoteMissionDataSource): MissionRemoteDataSource
    @Binds @Singleton abstract fun home(impl: RemoteHomeDataSource): HomeDataSource
    @Binds @Singleton abstract fun notification(impl: RemoteNotificationDataSource): NotificationRemoteDataSource
    @Binds @Singleton abstract fun notice(impl: RemoteNoticeDataSource): NoticeRemoteDataSource
    @Binds @Singleton abstract fun runningRecord(impl: RemoteRunningRecordRepository): RunningRecordRepository
}

private class AuthTokenStore(private val local: AuthLocalDataSource) : TokenStore {
    override fun accessToken(): String? = runBlocking { local.getToken()?.accessToken }
    override fun refreshToken(): String? = runBlocking { local.getToken()?.refreshToken }
    override fun save(accessToken: String, refreshToken: String) = runBlocking {
        local.saveToken(AuthToken(accessToken, refreshToken, ACCESS_TOKEN_SECONDS))
    }
    override fun clear() = runBlocking { local.clearToken() }

    private companion object {
        const val ACCESS_TOKEN_SECONDS = 3600L
    }
}
