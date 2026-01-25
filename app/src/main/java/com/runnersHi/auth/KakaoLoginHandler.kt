package com.runnersHi.auth

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 카카오 로그인 핸들러
 */
object KakaoLoginHandler {

    // TODO: 실제 SDK 연동 시 false로 변경
    private const val USE_MOCK = true

    /**
     * 카카오 로그인 실행
     * @return 카카오 액세스 토큰
     */
    suspend fun login(context: Context): String {
        // Mock 모드: delay 후 mock 토큰 반환
        if (USE_MOCK) {
            delay(1500)
            return "mock_kakao_access_token_${System.currentTimeMillis()}"
        }

        // 실제 카카오 SDK 로그인
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

            // 카카오톡 설치 여부 확인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                // 카카오톡으로 로그인
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            continuation.resumeWithException(error)
                            return@loginWithKakaoTalk
                        }
                        // 카카오톡에 연결된 카카오 계정이 없는 경우, 카카오 계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        continuation.resume(token.accessToken)
                    }
                }
            } else {
                // 카카오톡 미설치 시 카카오 계정으로 로그인
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }

    /**
     * 카카오 로그아웃
     */
    suspend fun logout(): Unit = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.logout { error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else {
                continuation.resume(Unit)
            }
        }
    }

    /**
     * 카카오 연결 끊기 (회원 탈퇴)
     */
    suspend fun unlink(): Unit = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else {
                continuation.resume(Unit)
            }
        }
    }
}
