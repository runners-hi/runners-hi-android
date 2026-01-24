package com.runnersHi.domain.auth.repository

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult

interface AuthRepository {
    suspend fun getStoredToken(): AuthToken?
    suspend fun refreshToken(refreshToken: String): Result<AuthToken>
    suspend fun saveToken(token: AuthToken)
    suspend fun clearToken()
    fun isLoggedIn(): Boolean
    suspend fun loginWithSocial(type: SocialLoginType, socialToken: String): Result<LoginResult>
}
