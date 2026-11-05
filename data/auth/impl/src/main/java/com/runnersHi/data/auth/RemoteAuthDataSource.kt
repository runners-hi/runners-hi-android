package com.runnersHi.data.auth

import com.runnersHi.core.network.ApiException
import com.runnersHi.core.network.AppleLoginRequest
import com.runnersHi.core.network.KakaoLoginRequest
import com.runnersHi.core.network.RefreshTokenRequest
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.core.network.apiCallNullable
import com.runnersHi.domain.auth.model.AppleCredential
import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val api: RunnersHiApi
) : AuthRemoteDataSource {

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        val tokens = try {
            apiCall { api.refreshToken(RefreshTokenRequest(refreshToken)) }
        } catch (e: ApiException) {
            if (e.httpStatus in 400..499) throw TokenExpiredException(e.message.orEmpty())
            throw e
        }
        return AuthToken(tokens.accessToken, tokens.refreshToken, ACCESS_TOKEN_SECONDS)
    }

    override suspend fun loginWithSocial(type: SocialLoginType, token: String): SocialLoginResponse {
        val response = when (type) {
            SocialLoginType.KAKAO -> apiCall { api.loginWithKakao(KakaoLoginRequest(token)) }
            SocialLoginType.APPLE -> {
                val credential = AppleCredential.fromSocialToken(token)
                    ?: throw ApiException("INVALID_APPLE_TOKEN", 400, "Apple 로그인 정보를 확인할 수 없어요")
                apiCall { api.loginWithAppleWeb(AppleLoginRequest(credential.idToken, credential.authorizationCode)) }
            }
        }
        return SocialLoginResponse(
            result = if (response.isNewUser) LoginResult.NewUser(userId = "") else LoginResult.Success,
            token = AuthToken(response.accessToken, response.refreshToken, ACCESS_TOKEN_SECONDS)
        )
    }

    override suspend fun logout() {
        apiCallNullable { api.logout() }
    }

    override suspend fun withdraw() {
        apiCallNullable { api.withdraw() }
    }

    private companion object {
        const val ACCESS_TOKEN_SECONDS = 3600L
    }
}
