package com.runnersHi.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import com.runnersHi.BuildConfig
import com.runnersHi.domain.auth.model.SocialLoginCancelledException
import java.util.UUID

/**
 * Apple 로그인 웹 화면. 결과 주소로 넘어가는 순간 가로채 토큰을 읽고 닫는다.
 * 결과 주소는 실제로 열리지 않으므로 그 주소에 서버 코드가 없어도 된다.
 */
class AppleLoginActivity : ComponentActivity() {

    private val state = UUID.randomUUID().toString()
    private var finished = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppleLoginHandler.pending == null) {
            finish()
            return
        }

        val webView = WebView(this).apply {
            fitsSystemWindows = true
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                    handleUrl(request.url.toString())

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    handleUrl(url)
                }
            }
        }
        setContentView(webView)
        onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) webView.goBack() else complete { cancel() }
        }
        // 이전 계정으로 자동 로그인되지 않게 한다.
        CookieManager.getInstance().removeAllCookies {
            webView.loadUrl(
                AppleAuthUrl.authorize(
                    baseUrl = BuildConfig.APPLE_AUTHORIZE_URL,
                    serviceId = BuildConfig.APPLE_SERVICE_ID,
                    redirectUri = BuildConfig.APPLE_REDIRECT_URI,
                    state = state
                )
            )
        }
    }

    private fun handleUrl(url: String): Boolean {
        val result = AppleAuthUrl.parseRedirect(url, BuildConfig.APPLE_REDIRECT_URI, state) ?: return false
        when (result) {
            is AppleAuthUrl.Result.Success -> complete { AppleLoginHandler.pending?.complete(result.credential) }
            AppleAuthUrl.Result.Cancelled -> complete { cancel() }
            is AppleAuthUrl.Result.Failure -> complete {
                AppleLoginHandler.pending?.completeExceptionally(IllegalStateException(result.reason))
            }
        }
        return true
    }

    private fun cancel() {
        AppleLoginHandler.pending?.completeExceptionally(SocialLoginCancelledException())
    }

    private inline fun complete(block: () -> Unit) {
        if (finished) return
        finished = true
        block()
        finish()
    }

    override fun onDestroy() {
        // 시스템이 화면을 닫아도 로그인 버튼이 로딩에 멈추지 않게 한다.
        if (!isChangingConfigurations) complete { cancel() }
        super.onDestroy()
    }
}
