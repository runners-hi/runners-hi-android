package com.runnersHi

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.runnersHi.analytics.FirebaseAnalyticsTracker
import com.runnersHi.core.common.analytics.AnalyticsHub
import com.runnersHi.debug.ServerSelection
import com.runnersHi.push.RunnersHiMessagingService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RunnersHiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 디버그 재시작용 별도 프로세스(:debug_restart)에서는 아무것도 초기화하지 않는다(Firebase 등이 없다).
        if (getProcessName() != packageName) return

        RunnersHiMessagingService.createChannel(this)

        ServerSelection.init(this)

        AnalyticsHub.appStartMs = System.currentTimeMillis()
        val appPrefs = getSharedPreferences("analytics", MODE_PRIVATE)
        AnalyticsHub.isFirstLaunch = !appPrefs.getBoolean("launched_before", false)
        appPrefs.edit().putBoolean("launched_before", true).apply()

        // 행동 로그: 릴리스만 수집한다. 디버그·스테이징은 꺼 두고, 디버그 메뉴 토글로만 켠다.
        AnalyticsHub.delegate = FirebaseAnalyticsTracker(this)
        AnalyticsHub.setCollectionEnabled(ServerSelection.analyticsEnabled(this))

        // Kakao SDK 초기화 (키가 없으면 카카오 로그인은 사용할 수 없다)
        val kakaoAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        if (kakaoAppKey.isNotBlank()) {
            KakaoSdk.init(this, kakaoAppKey)
            Log.d("RunnersHiApp", "Kakao SDK initialized")
        } else {
            Log.w("RunnersHiApp", "Kakao SDK not initialized - no app key")
        }
    }
}
