package com.runnersHi.di

import android.content.Context
import com.runnersHi.data.health.HealthConnectDataSource
import com.runnersHi.data.health.HealthDataSource
import com.runnersHi.data.health.HealthRepositoryImpl
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
        return HealthConnectDataSource(context)
    }

    @Provides
    @Singleton
    fun provideHealthRepository(
        healthDataSource: HealthDataSource
    ): HealthRepository {
        return HealthRepositoryImpl(healthDataSource)
    }
}
