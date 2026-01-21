package com.runnersHi.di

import com.runnersHi.domain.usecase.CheckAppVersionUseCase
import com.runnersHi.domain.usecase.CheckAppVersionUseCaseImpl
import com.runnersHi.domain.usecase.CheckLoginStatusUseCase
import com.runnersHi.domain.usecase.CheckLoginStatusUseCaseImpl
import com.runnersHi.domain.usecase.GetCurrentUserUseCase
import com.runnersHi.domain.usecase.GetCurrentUserUseCaseImpl
import com.runnersHi.domain.usecase.GetWeeklyRankingUseCase
import com.runnersHi.domain.usecase.GetWeeklyRankingUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindGetCurrentUserUseCase(
        impl: GetCurrentUserUseCaseImpl
    ): GetCurrentUserUseCase

    @Binds
    abstract fun bindGetWeeklyRankingUseCase(
        impl: GetWeeklyRankingUseCaseImpl
    ): GetWeeklyRankingUseCase

    @Binds
    abstract fun bindCheckAppVersionUseCase(
        impl: CheckAppVersionUseCaseImpl
    ): CheckAppVersionUseCase

    @Binds
    abstract fun bindCheckLoginStatusUseCase(
        impl: CheckLoginStatusUseCaseImpl
    ): CheckLoginStatusUseCase
}
