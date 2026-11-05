package com.runnersHi.auth

import android.content.Context
import com.runnersHi.BuildConfig
import com.runnersHi.domain.auth.model.SocialLoginCancelledException
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

    // 앱 키가 없으면 SDK가 초기화되지 않으므로 Mock 로그인으로 동작
    private val USE_MOCK = BuildConfig.KAKAO_NATIVE_APP_KEY.isBlank()

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
            val finish: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                when {
                    !continuation.isActive -> Unit
                    token != null -> continuation.resume(token.accessToken)
                    error.isCancelled() -> continuation.resumeWithException(SocialLoginCancelledException())
                    else -> continuation.resumeWithException(error ?: IllegalStateException("Token is null"))
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null && !error.isCancelled()) {
                        // 카카오톡에 연결된 계정이 없으면 카카오 계정으로 로그인한다.
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = finish)
                    } else {
                        finish(token, error)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = finish)
            }
        }
    }

    private fun Throwable?.isCancelled(): Boolean =
        this is ClientError && reason == ClientErrorCause.Cancelled

    /**
     * 카카오 로그아웃
     */
    suspend fun logout() {
        if (USE_MOCK) return
        logoutFromSdk()
    }

    private suspend fun logoutFromSdk(): Unit = suspendCancellableCoroutine { continuation ->
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
    suspend fun unlink() {
        if (USE_MOCK) return
        unlinkFromSdk()
    }

    private suspend fun unlinkFromSdk(): Unit = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else {
                continuation.resume(Unit)
            }
        }
    }
}
