package com.runnersHi.debug

import android.content.Context
import com.runnersHi.BuildConfig
import com.runnersHi.core.network.ServerEndpoint
import java.io.File

/** 디버그 빌드의 서버 선택. 고른 값은 SharedPreferences에 저장한다(릴리스에는 이 코드가 없다). */
object ServerSelection {
    const val PROD_URL = ServerEndpoint.PROD_URL
    const val DEV_URL = "https://runnershi-dev-api.oguriworks.com"
    /** 안드로이드 에뮬레이터에서 맥의 프록시(포트 8899)로 가는 주소 */
    const val DEFAULT_CUSTOM_URL = "http://10.0.2.2:8899"

    /** 개발 서버 로그인 코드(심사 로그인 경로). 서버가 알려 주는 값으로 맞춘다. */
    val DEV_LOGIN_CODE: String? = "seed01" // /api/auth/dev userKey(시드 회원)

    private const val PREFS = "debug_server"
    private const val KEY_URL = "url"
    private const val KEY_ANALYTICS = "analytics"

    /** 호스팅 모의 프록시. 세션 이름별로 규칙이 따로 쌓인다. */
    const val MOCK_BASE_URL = "https://runnershi-mock.oguriworks.com/s/"

    /** 테스터 배포용(staging) 빌드는 처음부터 개발 서버가 골라져 있다. */
    private val defaultUrl: String? = if (BuildConfig.BUILD_TYPE == "staging") DEV_URL else null

    /** DebugView 확인용 수집 토글(기본 꺼짐). 디버그·스테이징 빌드는 평소 수집하지 않는다. */
    fun analyticsEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ANALYTICS, false)

    fun setAnalyticsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ANALYTICS, enabled).commit()
        com.runnersHi.core.common.analytics.AnalyticsHub.setCollectionEnabled(enabled)
    }

    fun init(context: Context) {
        current(context)?.let { ServerEndpoint.baseUrl = it }
    }

    /** 사용자가 고른 값, 없으면 빌드 기본값. */
    fun current(context: Context): String? = saved(context) ?: defaultUrl

    fun saved(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_URL, null)

    /** 세션 이름 기본값: 기기 모델(영숫자만). */
    fun defaultSession(): String =
        android.os.Build.MODEL.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty { "device" }

    fun mockUrl(session: String): String =
        MOCK_BASE_URL + session.trim().lowercase().replace(Regex("[^a-z0-9-]+"), "-").trim('-').ifEmpty { defaultSession() }

    fun label(url: String): String = when (url) {
        PROD_URL -> "PROD"
        DEV_URL -> "DEV"
        else -> if (url.startsWith(MOCK_BASE_URL)) "MOCK ${url.removePrefix(MOCK_BASE_URL)}" else "CUSTOM ${url.removePrefix("http://").removePrefix("https://")}"
    }

    /** 처음 고를 때: 지울 로컬 상태가 없으니 바로 적용한다. */
    fun selectFirst(context: Context, url: String) {
        save(context, url)
        ServerEndpoint.baseUrl = url
    }

    /** 다른 서버로 바꿀 때: 토큰·지역·약관 등 로컬 상태를 지우고 앱을 처음부터 다시 띄운다. */
    fun switchAndRestart(context: Context, url: String) {
        save(context, url)
        wipeLocalState(context)
        DebugRestartActivity.restart(context)
    }

    private fun save(context: Context, url: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_URL, url).commit()
    }

    private fun wipeLocalState(context: Context) {
        val dataDir = File(context.applicationInfo.dataDir)
        File(dataDir, "shared_prefs").listFiles()
            ?.filter { it.name != "$PREFS.xml" }
            ?.forEach { it.delete() }
        File(dataDir, "datastore").deleteRecursively()
        File(dataDir, "files").deleteRecursively()
        context.cacheDir.deleteRecursively()
    }
}
