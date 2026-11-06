package com.runnersHi.auth

import android.content.Context
import com.runnersHi.BuildConfig
import com.runnersHi.domain.auth.model.SocialLoginCancelledException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 카카오 로그인 핸들러
 */
object KakaoLoginHandler {

    // 앱 키가 없으면 SDK가 초기화되지 않아 로그인할 수 없다.
    private val isConfigured = BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()

    /**
     * 카카오 로그인 실행
     * @return 카카오 액세스 토큰
     */
    suspend fun login(context: Context): String {
        check(isConfigured) { "카카오 로그인 설정이 없어요" }

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
        if (!isConfigured) return
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
        if (!isConfigured) return
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
