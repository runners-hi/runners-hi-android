package com.runnersHi.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.runnersHi.BuildConfig
import com.runnersHi.core.common.analytics.Analytics

/** Firebase Analytics(GA4) 구현. 파라미터는 문자열·숫자·불린만 넘긴다(불린은 true/false 문자열). */
class FirebaseAnalyticsTracker(context: Context) : Analytics {
    private val firebase = FirebaseAnalytics.getInstance(context).also {
        // 플랫폼·빌드 구분(iOS와 같은 값 규칙: android/ios)
        it.setUserProperty("app_platform", "android")
        it.setUserProperty("build", com.runnersHi.BuildConfig.BUILD_TYPE)
    }

    private val crashlytics = FirebaseCrashlytics.getInstance().also {
        it.setCustomKey("app_platform", "android")
        it.setCustomKey("build", BuildConfig.BUILD_TYPE)
        it.setCustomKey("version", BuildConfig.VERSION_NAME)
        // 디버그·스테이징은 크래시 기록도 끈다(릴리스만 수집).
        it.setCrashlyticsCollectionEnabled(BuildConfig.BUILD_TYPE == "release")
    }

    override fun log(name: String, params: Map<String, Any?>) {
        val bundle = Bundle()
        // 모든 이벤트에 플랫폼·빌드를 파라미터로도 붙인다(사용자 속성과 함께).
        bundle.putString("app_platform", "android")
        bundle.putString("build", BuildConfig.BUILD_TYPE)
        crashlytics.log("$name ${params.entries.joinToString(" ") { "${it.key}=${it.value}" }}".take(200))
        params.forEach { (key, value) ->
            when (value) {
                null -> Unit
                is Int -> bundle.putLong(key, value.toLong())
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putString(key, value.toString())
                else -> bundle.putString(key, value.toString().take(100))
            }
        }
        firebase.logEvent(name, bundle)
    }

    override fun setUserId(userId: String?) {
        firebase.setUserId(userId)
        crashlytics.setUserId(userId ?: "")
    }

    override fun recordError(throwable: Throwable) = crashlytics.recordException(throwable)

    override fun setCurrentScreen(name: String) = crashlytics.setCustomKey("screen", name)

    override fun setCollectionEnabled(enabled: Boolean) = firebase.setAnalyticsCollectionEnabled(enabled)
}
