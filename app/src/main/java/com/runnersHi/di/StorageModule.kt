package com.runnersHi.di

import android.content.Context
import com.runnersHi.data.auth.AuthLocalDataSource
import com.runnersHi.data.auth.EncryptedAuthLocalDataSource
import com.runnersHi.data.region.PrefsRegionLocalDataSource
import com.runnersHi.data.region.RegionLocalDataSource
import com.runnersHi.data.terms.PrefsTermsLocalDataSource
import com.runnersHi.data.terms.TermsLocalDataSource
import com.runnersHi.data.user.PrefsUserLocalDataSource
import com.runnersHi.data.user.UserLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideAuthLocalDataSource(
        @ApplicationContext context: Context
    ): AuthLocalDataSource = EncryptedAuthLocalDataSource(context)

    @Provides
    @Singleton
    fun provideUserLocalDataSource(
        @ApplicationContext context: Context
    ): UserLocalDataSource = PrefsUserLocalDataSource(context)

    @Provides
    @Singleton
    fun provideRegionLocalDataSource(
        @ApplicationContext context: Context
    ): RegionLocalDataSource = PrefsRegionLocalDataSource(context)

    @Provides
    @Singleton
    fun provideTermsLocalDataSource(
        @ApplicationContext context: Context
    ): TermsLocalDataSource = PrefsTermsLocalDataSource(context)
}
