package com.runnersHi.data.datasource

import com.runnersHi.domain.model.AuthToken

interface AuthLocalDataSource {
    suspend fun getToken(): AuthToken?
    suspend fun saveToken(token: AuthToken)
    suspend fun clearToken()
    fun hasToken(): Boolean
}

interface AuthRemoteDataSource {
    suspend fun refreshToken(refreshToken: String): AuthToken
}
