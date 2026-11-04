package com.runnersHi.di

import android.content.Context
import com.runnersHi.data.auth.AuthLocalDataSource
import com.runnersHi.data.auth.EncryptedAuthLocalDataSource
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
}
