package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 서버 연동 전 Mock 인증 API. 토큰 저장은 [EncryptedAuthLocalDataSource]가 담당한다.
 */
@Singleton
class MockAuthRemoteDataSource @Inject constructor() : AuthRemoteDataSource {

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        // 네트워크 지연 시뮬레이션
        delay(300)

        // Mock: refreshToken이 "expired"면 실패, 그 외는 성공
        if (refreshToken == "expired") {
            throw TokenExpiredException("Refresh token has expired")
        }

        return createToken()
    }

    override suspend fun loginWithSocial(type: SocialLoginType, token: String): SocialLoginResponse {
        // 네트워크 지연 시뮬레이션
        delay(1000)

        // Mock: 항상 신규 유저로 응답해 이용약관 화면으로 이동한다.
        return SocialLoginResponse(
            result = LoginResult.NewUser(userId = "mock_user_${System.currentTimeMillis()}"),
            token = createToken()
        )
    }

    private fun createToken() = AuthToken(
        accessToken = "mock_access_token_${System.currentTimeMillis()}",
        refreshToken = "mock_refresh_token_${System.currentTimeMillis()}",
        expiresIn = 3600
    )
}

class TokenExpiredException(message: String) : Exception(message)
