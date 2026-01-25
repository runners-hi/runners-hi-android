package com.runnersHi.auth

import android.content.Context
import kotlinx.coroutines.delay

/**
 * 카카오 로그인 핸들러
 *
 * TODO: 실제 카카오 SDK 연동 시 주석 해제
 * 현재는 Mock 모드로 동작
 */
object KakaoLoginHandler {

    // Mock 모드 플래그 (실제 연동 시 false로 변경)
    private const val USE_MOCK = true

    /**
     * 카카오 로그인 실행
     * @return 카카오 액세스 토큰
     */
    suspend fun login(context: Context): String {
        if (USE_MOCK) {
            // Mock: 로그인 지연 시뮬레이션
            delay(1500)
            return "mock_kakao_access_token_${System.currentTimeMillis()}"
        }

        // TODO: 실제 카카오 SDK 연동
        /*
        return suspendCancellableCoroutine { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    continuation.resume(token.accessToken)
                } else {
                    continuation.resumeWithException(IllegalStateException("Token is null"))
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            continuation.resumeWithException(error)
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        continuation.resume(token.accessToken)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
        */
        throw NotImplementedError("Kakao SDK not configured")
    }

    /**
     * 카카오 로그아웃
     */
    suspend fun logout() {
        if (USE_MOCK) {
            delay(300)
            return
        }
        // TODO: 실제 구현
    }

    /**
     * 카카오 연결 끊기 (회원 탈퇴)
     */
    suspend fun unlink() {
        if (USE_MOCK) {
            delay(300)
            return
        }
        // TODO: 실제 구현
    }
}
