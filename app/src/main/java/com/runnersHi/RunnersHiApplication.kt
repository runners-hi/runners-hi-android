package com.runnersHi

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RunnersHiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화 (키가 없으면 Mock 모드로 동작)
        val kakaoAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        if (kakaoAppKey.isNotBlank()) {
            KakaoSdk.init(this, kakaoAppKey)
            Log.d("RunnersHiApp", "Kakao SDK initialized")
        } else {
            Log.w("RunnersHiApp", "Kakao SDK not initialized - using mock mode")
        }
    }
}
