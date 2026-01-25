package com.runnersHi.di

import com.runnersHi.data.auth.AuthLocalDataSource
import com.runnersHi.data.auth.AuthRemoteDataSource
import com.runnersHi.data.auth.AuthRepositoryImpl
import com.runnersHi.data.auth.MockAuthLocalDataSource
import com.runnersHi.data.auth.MockAuthRemoteDataSource
import com.runnersHi.data.ranking.MockRankingLocalDataSource
import com.runnersHi.data.ranking.MockRankingRemoteDataSource
import com.runnersHi.data.ranking.RankingLocalDataSource
import com.runnersHi.data.ranking.RankingRemoteDataSource
import com.runnersHi.data.ranking.RankingRepositoryImpl
import com.runnersHi.data.splash.AppConfigRemoteDataSource
import com.runnersHi.data.splash.AppConfigRepositoryImpl
import com.runnersHi.data.splash.MockAppConfigRemoteDataSource
import com.runnersHi.data.user.MockUserLocalDataSource
import com.runnersHi.data.user.MockUserRemoteDataSource
import com.runnersHi.data.user.UserLocalDataSource
import com.runnersHi.data.user.UserRemoteDataSource
import com.runnersHi.data.user.UserRepositoryImpl
import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.ranking.repository.RankingRepository
import com.runnersHi.domain.splash.repository.AppConfigRepository
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
    abstract fun bindAuthLocalDataSource(
        impl: MockAuthLocalDataSource
    ): AuthLocalDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: MockAuthRemoteDataSource
    ): AuthRemoteDataSource

    // DataSources - User
    @Binds
    @Singleton
    abstract fun bindUserLocalDataSource(
        impl: MockUserLocalDataSource
    ): UserLocalDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        impl: MockUserRemoteDataSource
    ): UserRemoteDataSource

    // DataSources - Ranking
    @Binds
    @Singleton
    abstract fun bindRankingLocalDataSource(
        impl: MockRankingLocalDataSource
    ): RankingLocalDataSource

    @Binds
    @Singleton
    abstract fun bindRankingRemoteDataSource(
        impl: MockRankingRemoteDataSource
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
}
