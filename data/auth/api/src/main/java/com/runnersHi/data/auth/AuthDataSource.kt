package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult

interface AuthLocalDataSource {
    suspend fun getToken(): AuthToken?
    suspend fun saveToken(token: AuthToken)
    suspend fun clearToken()
    fun hasToken(): Boolean
}

interface AuthRemoteDataSource {
    suspend fun refreshToken(refreshToken: String): AuthToken
    suspend fun loginWithSocial(type: SocialLoginType, socialToken: String): Pair<AuthToken, LoginResult>
}
