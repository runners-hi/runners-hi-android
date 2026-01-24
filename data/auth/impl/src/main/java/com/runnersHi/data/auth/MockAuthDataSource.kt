package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthLocalDataSource @Inject constructor() : AuthLocalDataSource {

    private var storedToken: AuthToken? = null

    override suspend fun getToken(): AuthToken? {
        // 토큰 조회 지연 시뮬레이션 (progress 확인용)
        delay(800)
        return storedToken
    }

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

    override suspend fun loginWithSocial(
        type: SocialLoginType,
        socialToken: String
    ): Pair<AuthToken, LoginResult> {
        // 네트워크 지연 시뮬레이션
        delay(1000)

        // Mock: 소셜 토큰이 "error"면 실패
        if (socialToken == "error") {
            throw SocialLoginException("소셜 로그인에 실패했습니다.")
        }

        val token = AuthToken(
            accessToken = "access_${type.name.lowercase()}_${System.currentTimeMillis()}",
            refreshToken = "refresh_${type.name.lowercase()}_${System.currentTimeMillis()}",
            expiresIn = 3600
        )

        // Mock: 50% 확률로 신규 사용자
        val loginResult = if (System.currentTimeMillis() % 2 == 0L) {
            LoginResult.NewUser(userId = "user_${System.currentTimeMillis()}")
        } else {
            LoginResult.Success
        }

        return Pair(token, loginResult)
    }
}

class TokenExpiredException(message: String) : Exception(message)
class SocialLoginException(message: String) : Exception(message)
