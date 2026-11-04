package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.usecase.LoginResult

/**
 * 소셜 로그인 응답: 화면 분기용 결과와 저장할 토큰
 */
data class SocialLoginResponse(
    val result: LoginResult,
    val token: AuthToken?
)
