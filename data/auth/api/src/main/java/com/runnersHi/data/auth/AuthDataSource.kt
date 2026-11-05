package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType

interface AuthLocalDataSource {
    suspend fun getToken(): AuthToken?
    suspend fun saveToken(token: AuthToken)
    suspend fun clearToken()
    fun hasToken(): Boolean
}

interface AuthRemoteDataSource {
    suspend fun refreshToken(refreshToken: String): AuthToken
    suspend fun loginWithSocial(type: SocialLoginType, token: String): SocialLoginResponse
    suspend fun logout() {}
    suspend fun withdraw() {}
}
