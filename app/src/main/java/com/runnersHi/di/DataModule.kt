package com.runnersHi.di

import com.runnersHi.data.datasource.AppConfigRemoteDataSource
import com.runnersHi.data.datasource.AuthLocalDataSource
import com.runnersHi.data.datasource.AuthRemoteDataSource
import com.runnersHi.data.datasource.RankingLocalDataSource
import com.runnersHi.data.datasource.RankingRemoteDataSource
import com.runnersHi.data.datasource.UserLocalDataSource
import com.runnersHi.data.datasource.UserRemoteDataSource
import com.runnersHi.data.datasource.mock.MockAppConfigRemoteDataSource
import com.runnersHi.data.datasource.mock.MockAuthLocalDataSource
import com.runnersHi.data.datasource.mock.MockAuthRemoteDataSource
import com.runnersHi.data.datasource.mock.MockRankingLocalDataSource
import com.runnersHi.data.datasource.mock.MockRankingRemoteDataSource
import com.runnersHi.data.datasource.mock.MockUserLocalDataSource
import com.runnersHi.data.datasource.mock.MockUserRemoteDataSource
import com.runnersHi.data.repository.AppConfigRepositoryImpl
import com.runnersHi.data.repository.AuthRepositoryImpl
import com.runnersHi.data.repository.RankingRepositoryImpl
import com.runnersHi.data.repository.UserRepositoryImpl
import com.runnersHi.domain.repository.AppConfigRepository
import com.runnersHi.domain.repository.AuthRepository
import com.runnersHi.domain.repository.RankingRepository
import com.runnersHi.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

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

    // DataSources - AppConfig
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

    // Repositories
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
}
