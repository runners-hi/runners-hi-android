package com.runnersHi.domain.auth.repository

import com.runnersHi.domain.auth.model.AuthToken

interface AuthRepository {
    suspend fun getStoredToken(): AuthToken?
    suspend fun refreshToken(refreshToken: String): Result<AuthToken>
    suspend fun saveToken(token: AuthToken)
    suspend fun clearToken()
    fun isLoggedIn(): Boolean
}
