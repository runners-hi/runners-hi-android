package com.runnersHi.data.repository

import com.runnersHi.data.datasource.AuthLocalDataSource
import com.runnersHi.data.datasource.AuthRemoteDataSource
import com.runnersHi.domain.model.AuthToken
import com.runnersHi.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val localDataSource: AuthLocalDataSource,
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun getStoredToken(): AuthToken? {
        return localDataSource.getToken()
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthToken> {
        return try {
            val newToken = remoteDataSource.refreshToken(refreshToken)
            localDataSource.saveToken(newToken)
            Result.success(newToken)
        } catch (e: Exception) {
            localDataSource.clearToken()
            Result.failure(e)
        }
    }

    override suspend fun saveToken(token: AuthToken) {
        localDataSource.saveToken(token)
    }

    override suspend fun clearToken() {
        localDataSource.clearToken()
    }

    override fun isLoggedIn(): Boolean {
        return localDataSource.hasToken()
    }
}
