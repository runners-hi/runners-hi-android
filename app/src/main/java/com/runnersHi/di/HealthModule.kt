package com.runnersHi.di

import android.content.Context
import com.runnersHi.BuildConfig
import com.runnersHi.data.health.HealthConnectDataSource
import com.runnersHi.data.health.HealthDataSource
import com.runnersHi.data.health.HealthRepositoryImpl
import com.runnersHi.data.health.MockHealthDataSource
import com.runnersHi.domain.health.repository.HealthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthModule {

    @Provides
    @Singleton
    fun provideHealthDataSource(
        @ApplicationContext context: Context
    ): HealthDataSource {
        // 디버그 빌드는 목업 기록을 쓴다. 단, https 실서버로 붙으면 가짜 기록이 올라가지 않게 실제 기록만 쓴다.
        val useMock = BuildConfig.DEBUG && !BuildConfig.API_BASE_URL.startsWith("https://")
        return if (useMock) MockHealthDataSource() else HealthConnectDataSource(context)
    }

    @Provides
    @Singleton
    fun provideHealthRepository(
        healthDataSource: HealthDataSource
    ): HealthRepository {
        return HealthRepositoryImpl(healthDataSource)
    }
}
