package com.runnersHi.domain.repository

import com.runnersHi.domain.model.AuthToken

interface AuthRepository {
    suspend fun getStoredToken(): AuthToken?
    suspend fun refreshToken(refreshToken: String): Result<AuthToken>
    suspend fun saveToken(token: AuthToken)
    suspend fun clearToken()
    fun isLoggedIn(): Boolean
}
