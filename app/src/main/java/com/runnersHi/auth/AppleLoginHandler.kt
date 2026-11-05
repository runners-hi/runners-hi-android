package com.runnersHi.auth

import android.content.Context
import android.content.Intent
import com.runnersHi.BuildConfig
import com.runnersHi.domain.auth.model.AppleCredential
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay

/**
 * Apple 로그인 핸들러
 *
 * 안드로이드에는 Apple 네이티브 로그인이 없어 웹 로그인을 쓴다.
 * 실서버에서는 APPLE_SERVICE_ID와 APPLE_REDIRECT_URI가 있어야 동작하고, 없으면 버튼을 숨긴다.
 */
object AppleLoginHandler {

    private val isMock = BuildConfig.API_BASE_URL.isBlank()
    private val isConfigured =
        BuildConfig.APPLE_SERVICE_ID.isNotBlank() && BuildConfig.APPLE_REDIRECT_URI.isNotBlank()

    val isAvailable: Boolean = isMock || isConfigured

    internal var pending: CompletableDeferred<AppleCredential>? = null
        private set

    /** @return 서버로 보낼 소셜 토큰 */
    suspend fun login(context: Context): String {
        if (isMock) {
            delay(1500)
            return "mock_apple_id_token_${System.currentTimeMillis()}"
        }
        pending?.cancel()
        val result = CompletableDeferred<AppleCredential>()
        pending = result
        context.startActivity(Intent(context, AppleLoginActivity::class.java))
        return result.await().toSocialToken()
    }
}
