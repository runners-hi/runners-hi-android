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
        // iOS DEBUG와 같이 디버그 빌드는 목업 기록을 쓴다.
        return if (BuildConfig.DEBUG) MockHealthDataSource() else HealthConnectDataSource(context)
    }

    @Provides
    @Singleton
    fun provideHealthRepository(
        healthDataSource: HealthDataSource
    ): HealthRepository {
        return HealthRepositoryImpl(healthDataSource)
    }
}
