package com.runnersHi.di

import com.runnersHi.domain.auth.CheckLoginStatusUseCaseImpl
import com.runnersHi.domain.auth.GetStoredTokenUseCaseImpl
import com.runnersHi.domain.auth.LoginWithSocialUseCaseImpl
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.GetStoredTokenUseCase
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import com.runnersHi.domain.auth.LogoutUseCaseImpl
import com.runnersHi.domain.auth.usecase.LogoutUseCase
import com.runnersHi.domain.health.GetRunningRecordsUseCaseImpl
import com.runnersHi.domain.health.usecase.GetRunningRecordsUseCase
import com.runnersHi.domain.mission.GetMissionBoardUseCaseImpl
import com.runnersHi.domain.mission.usecase.GetMissionBoardUseCase
import com.runnersHi.domain.ranking.GetRankingBoardUseCaseImpl
import com.runnersHi.domain.ranking.usecase.GetRankingBoardUseCase
import com.runnersHi.domain.region.GetSelectedRegionUseCaseImpl
import com.runnersHi.domain.region.usecase.GetSelectedRegionUseCase
import com.runnersHi.domain.terms.HasAgreedTermsUseCaseImpl
import com.runnersHi.domain.terms.usecase.HasAgreedTermsUseCase
import com.runnersHi.domain.splash.CheckAppVersionUseCaseImpl
import com.runnersHi.domain.splash.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.terms.AgreeTermsUseCaseImpl
import com.runnersHi.domain.terms.GetTermsListUseCaseImpl
import com.runnersHi.domain.terms.usecase.AgreeTermsUseCase
import com.runnersHi.domain.terms.usecase.GetTermsListUseCase
import com.runnersHi.domain.health.CheckHealthPermissionUseCaseImpl
import com.runnersHi.domain.health.GetTodayRunningDataUseCaseImpl
import com.runnersHi.domain.health.GetWeekRunningDataUseCaseImpl
import com.runnersHi.domain.health.usecase.CheckHealthPermissionUseCase
import com.runnersHi.domain.health.usecase.GetTodayRunningDataUseCase
import com.runnersHi.domain.health.usecase.GetWeekRunningDataUseCase
import com.runnersHi.domain.home.GetHomeDataUseCaseImpl
import com.runnersHi.domain.home.usecase.GetHomeDataUseCase
import com.runnersHi.domain.region.SearchRegionsUseCaseImpl
import com.runnersHi.domain.region.SelectRegionUseCaseImpl
import com.runnersHi.domain.region.usecase.SearchRegionsUseCase
import com.runnersHi.domain.region.usecase.SelectRegionUseCase
import com.runnersHi.domain.user.GetCurrentUserUseCaseImpl
import com.runnersHi.domain.user.usecase.GetCurrentUserUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.runnersHi.domain.auth.WithdrawUseCaseImpl
import com.runnersHi.domain.auth.usecase.WithdrawUseCase
import com.runnersHi.domain.health.SyncRunningRecordsUseCaseImpl
import com.runnersHi.domain.health.usecase.SyncRunningRecordsUseCase
import com.runnersHi.domain.notice.GetNoticeDetailUseCaseImpl
import com.runnersHi.domain.notice.GetNoticesUseCaseImpl
import com.runnersHi.domain.notice.usecase.GetNoticeDetailUseCase
import com.runnersHi.domain.notice.usecase.GetNoticesUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindCheckAppVersionUseCase(
        impl: CheckAppVersionUseCaseImpl
    ): CheckAppVersionUseCase

    @Binds
    abstract fun bindCheckLoginStatusUseCase(
        impl: CheckLoginStatusUseCaseImpl
    ): CheckLoginStatusUseCase

    @Binds
    abstract fun bindGetStoredTokenUseCase(
        impl: GetStoredTokenUseCaseImpl
    ): GetStoredTokenUseCase

    @Binds
    abstract fun bindLoginWithSocialUseCase(
        impl: LoginWithSocialUseCaseImpl
    ): LoginWithSocialUseCase

    @Binds
    abstract fun bindGetCurrentUserUseCase(
        impl: GetCurrentUserUseCaseImpl
    ): GetCurrentUserUseCase

    @Binds
    abstract fun bindGetRankingBoardUseCase(
        impl: GetRankingBoardUseCaseImpl
    ): GetRankingBoardUseCase

    @Binds
    abstract fun bindGetMissionBoardUseCase(
        impl: GetMissionBoardUseCaseImpl
    ): GetMissionBoardUseCase

    @Binds
    abstract fun bindGetSelectedRegionUseCase(
        impl: GetSelectedRegionUseCaseImpl
    ): GetSelectedRegionUseCase

    @Binds
    abstract fun bindHasAgreedTermsUseCase(
        impl: HasAgreedTermsUseCaseImpl
    ): HasAgreedTermsUseCase

    @Binds
    abstract fun bindWithdrawUseCase(
        impl: WithdrawUseCaseImpl
    ): WithdrawUseCase

    @Binds
    abstract fun bindGetNoticesUseCase(
        impl: GetNoticesUseCaseImpl
    ): GetNoticesUseCase

    @Binds
    abstract fun bindGetNoticeDetailUseCase(
        impl: GetNoticeDetailUseCaseImpl
    ): GetNoticeDetailUseCase

    @Binds
    @Singleton
    abstract fun bindSyncRunningRecordsUseCase(
        impl: SyncRunningRecordsUseCaseImpl
    ): SyncRunningRecordsUseCase

    @Binds
    abstract fun bindLogoutUseCase(
        impl: LogoutUseCaseImpl
    ): LogoutUseCase

    @Binds
    abstract fun bindGetRunningRecordsUseCase(
        impl: GetRunningRecordsUseCaseImpl
    ): GetRunningRecordsUseCase

    @Binds
    abstract fun bindGetTermsListUseCase(
        impl: GetTermsListUseCaseImpl
    ): GetTermsListUseCase

    @Binds
    abstract fun bindAgreeTermsUseCase(
        impl: AgreeTermsUseCaseImpl
    ): AgreeTermsUseCase

    @Binds
    abstract fun bindSearchRegionsUseCase(
        impl: SearchRegionsUseCaseImpl
    ): SearchRegionsUseCase

    @Binds
    abstract fun bindSelectRegionUseCase(
        impl: SelectRegionUseCaseImpl
    ): SelectRegionUseCase

    @Binds
    abstract fun bindGetHomeDataUseCase(
        impl: GetHomeDataUseCaseImpl
    ): GetHomeDataUseCase

    @Binds
    abstract fun bindCheckHealthPermissionUseCase(
        impl: CheckHealthPermissionUseCaseImpl
    ): CheckHealthPermissionUseCase

    @Binds
    abstract fun bindGetTodayRunningDataUseCase(
        impl: GetTodayRunningDataUseCaseImpl
    ): GetTodayRunningDataUseCase

    @Binds
    abstract fun bindGetWeekRunningDataUseCase(
        impl: GetWeekRunningDataUseCaseImpl
    ): GetWeekRunningDataUseCase
}
