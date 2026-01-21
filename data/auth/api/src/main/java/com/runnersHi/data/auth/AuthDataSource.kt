package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken

interface AuthLocalDataSource {
    suspend fun getToken(): AuthToken?
    suspend fun saveToken(token: AuthToken)
    suspend fun clearToken()
    fun hasToken(): Boolean
}

interface AuthRemoteDataSource {
    suspend fun refreshToken(refreshToken: String): AuthToken
}
