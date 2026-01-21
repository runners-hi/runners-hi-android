package com.runnersHi.data.datasource.mock

import com.runnersHi.data.datasource.AuthLocalDataSource
import com.runnersHi.data.datasource.AuthRemoteDataSource
import com.runnersHi.domain.model.AuthToken
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthLocalDataSource @Inject constructor() : AuthLocalDataSource {

    private var storedToken: AuthToken? = null

    override suspend fun getToken(): AuthToken? = storedToken

    override suspend fun saveToken(token: AuthToken) {
        storedToken = token
    }

    override suspend fun clearToken() {
        storedToken = null
    }

    override fun hasToken(): Boolean = storedToken != null
}

@Singleton
class MockAuthRemoteDataSource @Inject constructor() : AuthRemoteDataSource {

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        // 네트워크 지연 시뮬레이션
        delay(300)

        // Mock: refreshToken이 "expired"면 실패, 그 외는 성공
        if (refreshToken == "expired") {
            throw TokenExpiredException("Refresh token has expired")
        }

        return AuthToken(
            accessToken = "new_access_token_${System.currentTimeMillis()}",
            refreshToken = "new_refresh_token_${System.currentTimeMillis()}",
            expiresIn = 3600
        )
    }
}

class TokenExpiredException(message: String) : Exception(message)
