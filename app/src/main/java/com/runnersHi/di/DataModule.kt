package com.runnersHi.di

import com.runnersHi.data.auth.AuthRepositoryImpl
import com.runnersHi.data.mission.MissionRepositoryImpl
import com.runnersHi.data.ranking.RankingRepositoryImpl
import com.runnersHi.data.splash.AppConfigRepositoryImpl
import com.runnersHi.data.terms.TermsRepositoryImpl
import com.runnersHi.data.home.HomeRepositoryImpl
import com.runnersHi.data.region.RegionRepositoryImpl
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
import com.runnersHi.data.notice.NoticeRepositoryImpl
import com.runnersHi.domain.notice.repository.NoticeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

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

    // Repositories - Terms
    @Binds
    @Singleton
    abstract fun bindTermsRepository(
        impl: TermsRepositoryImpl
    ): TermsRepository

    // Repositories - Region
    @Binds
    @Singleton
    abstract fun bindRegionRepository(
        impl: RegionRepositoryImpl
    ): RegionRepository

    // Mission
    @Binds
    @Singleton
    abstract fun bindMissionRepository(
        impl: MissionRepositoryImpl
    ): MissionRepository

    // Repositories - Home
    @Binds
    @Singleton
    abstract fun bindNoticeRepository(
        impl: NoticeRepositoryImpl
    ): NoticeRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository
}
