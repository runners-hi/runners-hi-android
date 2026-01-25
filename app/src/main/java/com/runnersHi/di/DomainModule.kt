package com.runnersHi.di

import com.runnersHi.domain.auth.CheckLoginStatusUseCaseImpl
import com.runnersHi.domain.auth.LoginWithSocialUseCaseImpl
import com.runnersHi.domain.auth.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.auth.usecase.LoginWithSocialUseCase
import com.runnersHi.domain.ranking.GetWeeklyRankingUseCaseImpl
import com.runnersHi.domain.ranking.usecase.GetWeeklyRankingUseCase
import com.runnersHi.domain.splash.CheckAppVersionUseCaseImpl
import com.runnersHi.domain.splash.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.user.GetCurrentUserUseCaseImpl
import com.runnersHi.domain.user.usecase.GetCurrentUserUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
    abstract fun bindLoginWithSocialUseCase(
        impl: LoginWithSocialUseCaseImpl
    ): LoginWithSocialUseCase

    @Binds
    abstract fun bindGetCurrentUserUseCase(
        impl: GetCurrentUserUseCaseImpl
    ): GetCurrentUserUseCase

    @Binds
    abstract fun bindGetWeeklyRankingUseCase(
        impl: GetWeeklyRankingUseCaseImpl
    ): GetWeeklyRankingUseCase
}
