package com.runnersHi.di

import com.runnersHi.data.auth.AuthRemoteDataSource
import com.runnersHi.data.auth.AuthRepositoryImpl
import com.runnersHi.data.auth.MockAuthRemoteDataSource
import com.runnersHi.data.mission.MissionRemoteDataSource
import com.runnersHi.data.mission.MissionRepositoryImpl
import com.runnersHi.data.mission.MockMissionDataSource
import com.runnersHi.data.ranking.MockRankingDataSource
import com.runnersHi.data.ranking.RankingRemoteDataSource
import com.runnersHi.data.ranking.RankingRepositoryImpl
import com.runnersHi.data.splash.AppConfigRemoteDataSource
import com.runnersHi.data.splash.AppConfigRepositoryImpl
import com.runnersHi.data.splash.MockAppConfigRemoteDataSource
import com.runnersHi.data.terms.MockTermsDataSource
import com.runnersHi.data.terms.TermsRemoteDataSource
import com.runnersHi.data.terms.TermsRepositoryImpl
import com.runnersHi.data.home.HomeDataSource
import com.runnersHi.data.home.HomeRepositoryImpl
import com.runnersHi.data.home.MockHomeDataSource
import com.runnersHi.data.region.MockRegionDataSource
import com.runnersHi.data.region.RegionRemoteDataSource
import com.runnersHi.data.region.RegionRepositoryImpl
import com.runnersHi.data.user.MockUserRemoteDataSource
import com.runnersHi.data.user.UserRemoteDataSource
import com.runnersHi.data.user.UserRepositoryImpl
import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.mission.repository.MissionRepository
import com.runnersHi.domain.ranking.repository.RankingRepository
import com.runnersHi.domain.splash.repository.AppConfigRepository
import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.home.repository.HomeRepository
import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.user.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // DataSources - Splash
    @Binds
    @Singleton
    abstract fun bindAppConfigRemoteDataSource(
        impl: MockAppConfigRemoteDataSource
    ): AppConfigRemoteDataSource

    // DataSources - Auth
    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: MockAuthRemoteDataSource
    ): AuthRemoteDataSource

    // DataSources - User
    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        impl: MockUserRemoteDataSource
    ): UserRemoteDataSource

    // DataSources - Ranking
    @Binds
    @Singleton
    abstract fun bindRankingRemoteDataSource(
        impl: MockRankingDataSource
    ): RankingRemoteDataSource

    // Repositories
    @Binds
    @Singleton
    abstract fun bindAppConfigRepository(
        impl: AppConfigRepositoryImpl
    ): AppConfigRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindRankingRepository(
        impl: RankingRepositoryImpl
    ): RankingRepository

    // DataSources - Terms
    @Binds
    @Singleton
    abstract fun bindTermsRemoteDataSource(
        impl: MockTermsDataSource
    ): TermsRemoteDataSource

    // Repositories - Terms
    @Binds
    @Singleton
    abstract fun bindTermsRepository(
        impl: TermsRepositoryImpl
    ): TermsRepository

    // DataSources - Region
    @Binds
    @Singleton
    abstract fun bindRegionRemoteDataSource(
        impl: MockRegionDataSource
    ): RegionRemoteDataSource

    // Repositories - Region
    @Binds
    @Singleton
    abstract fun bindRegionRepository(
        impl: RegionRepositoryImpl
    ): RegionRepository

    // Mission
    @Binds
    @Singleton
    abstract fun bindMissionRemoteDataSource(
        impl: MockMissionDataSource
    ): MissionRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMissionRepository(
        impl: MissionRepositoryImpl
    ): MissionRepository

    // DataSources - Home
    @Binds
    @Singleton
    abstract fun bindHomeDataSource(
        impl: MockHomeDataSource
    ): HomeDataSource

    // Repositories - Home
    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository
}
