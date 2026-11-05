package com.runnersHi.di

import com.runnersHi.BuildConfig
import com.runnersHi.core.network.NetworkFactory
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.TokenStore
import com.runnersHi.data.auth.AuthLocalDataSource
import com.runnersHi.data.auth.AuthRemoteDataSource
import com.runnersHi.data.auth.MockAuthRemoteDataSource
import com.runnersHi.data.auth.RemoteAuthDataSource
import com.runnersHi.data.health.NoOpRunningRecordRepository
import com.runnersHi.data.health.RemoteRunningRecordRepository
import com.runnersHi.data.home.HomeDataSource
import com.runnersHi.data.home.MockHomeDataSource
import com.runnersHi.data.home.RemoteHomeDataSource
import com.runnersHi.data.mission.MissionRemoteDataSource
import com.runnersHi.data.mission.MockMissionDataSource
import com.runnersHi.data.mission.RemoteMissionDataSource
import com.runnersHi.core.network.ImageUploader
import com.runnersHi.data.notification.EmptyNotificationDataSource
import com.runnersHi.data.notification.NotificationRemoteDataSource
import com.runnersHi.data.notification.RemoteNotificationDataSource
import com.runnersHi.data.notice.EmptyNoticeDataSource
import com.runnersHi.data.notice.NoticeRemoteDataSource
import com.runnersHi.data.notice.RemoteNoticeDataSource
import com.runnersHi.data.ranking.MockRankingDataSource
import com.runnersHi.data.ranking.RankingRemoteDataSource
import com.runnersHi.data.ranking.RemoteRankingDataSource
import com.runnersHi.data.region.MockRegionDataSource
import com.runnersHi.data.region.RegionRemoteDataSource
import com.runnersHi.data.region.RemoteRegionDataSource
import com.runnersHi.data.splash.AppConfigRemoteDataSource
import com.runnersHi.data.splash.MockAppConfigRemoteDataSource
import com.runnersHi.data.splash.RemoteAppConfigDataSource
import com.runnersHi.data.terms.MockTermsDataSource
import com.runnersHi.data.terms.RemoteTermsDataSource
import com.runnersHi.data.terms.TermsRemoteDataSource
import com.runnersHi.data.user.MockUserRemoteDataSource
import com.runnersHi.data.user.RemoteUserDataSource
import com.runnersHi.data.user.UserRemoteDataSource
import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.health.repository.RunningRecordRepository
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

/** 서버 주소가 있으면 실서버, 없으면 목업 데이터 소스를 쓴다. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val useServer = BuildConfig.API_BASE_URL.isNotBlank()

    private fun <T> choose(remote: Lazy<out T>, mock: Lazy<out T>): T =
        if (useServer) remote.get() else mock.get()

    @Provides
    @Singleton
    fun provideTokenStore(local: AuthLocalDataSource): TokenStore = AuthTokenStore(local)

    @Provides
    @Singleton
    fun provideApi(tokenStore: TokenStore): RunnersHiApi =
        NetworkFactory.createApi(BuildConfig.API_BASE_URL, tokenStore)

    @Provides
    @Singleton
    fun provideAppConfig(
        api: Lazy<RunnersHiApi>,
        mock: Lazy<MockAppConfigRemoteDataSource>
    ): AppConfigRemoteDataSource =
        if (useServer) RemoteAppConfigDataSource(api.get(), BuildConfig.VERSION_NAME) else mock.get()

    @Provides
    @Singleton
    fun provideAuth(remote: Lazy<RemoteAuthDataSource>, mock: Lazy<MockAuthRemoteDataSource>): AuthRemoteDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideUser(remote: Lazy<RemoteUserDataSource>, mock: Lazy<MockUserRemoteDataSource>): UserRemoteDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideRanking(remote: Lazy<RemoteRankingDataSource>, mock: Lazy<MockRankingDataSource>): RankingRemoteDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideTerms(remote: Lazy<RemoteTermsDataSource>, mock: Lazy<MockTermsDataSource>): TermsRemoteDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideRegion(remote: Lazy<RemoteRegionDataSource>, mock: Lazy<MockRegionDataSource>): RegionRemoteDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideMission(remote: Lazy<RemoteMissionDataSource>, mock: Lazy<MockMissionDataSource>): MissionRemoteDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideHome(remote: Lazy<RemoteHomeDataSource>, mock: Lazy<MockHomeDataSource>): HomeDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideImageUploader(): ImageUploader = ImageUploader()

    @Provides
    @Singleton
    fun provideNotification(
        remote: Lazy<RemoteNotificationDataSource>,
        mock: Lazy<EmptyNotificationDataSource>
    ): NotificationRemoteDataSource = choose(remote, mock)

    @Provides
    @Singleton
    fun provideNotice(remote: Lazy<RemoteNoticeDataSource>, mock: Lazy<EmptyNoticeDataSource>): NoticeRemoteDataSource =
        choose(remote, mock)

    @Provides
    @Singleton
    fun provideRunningRecord(
        remote: Lazy<RemoteRunningRecordRepository>,
        mock: Lazy<NoOpRunningRecordRepository>
    ): RunningRecordRepository = choose(remote, mock)
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
