package com.runnersHi.core.common.analytics

/**
 * 행동 로그 래퍼. 앱 코드는 이 인터페이스만 부른다(구현: Firebase Analytics, 테스트: [FakeAnalytics]).
 * 이름·파라미터는 iOS와 같은 계약을 따른다(snake_case). 이메일·닉네임·좌표는 넣지 않는다.
 */
interface Analytics {
    fun log(name: String, params: Map<String, Any?> = emptyMap())
    /** 서버 회원 id(숫자)만. 로그아웃 시 null. */
    fun setUserId(userId: String?)
    fun setCollectionEnabled(enabled: Boolean)
    /** 잡힌 오류를 크래시 기록에 non-fatal로 남긴다. 기본은 아무것도 안 한다. */
    fun recordError(throwable: Throwable) = Unit
    /** 크래시 기록에 붙는 현재 화면(custom key). */
    fun setCurrentScreen(name: String) = Unit
}

object NoOpAnalytics : Analytics {
    override fun log(name: String, params: Map<String, Any?>) = Unit
    override fun setUserId(userId: String?) = Unit
    override fun setCollectionEnabled(enabled: Boolean) = Unit
}

/** 테스트용: 불린 이벤트를 모아 둔다. */
class FakeAnalytics : Analytics {
    data class Event(val name: String, val params: Map<String, Any?>)

    val events = mutableListOf<Event>()
    var lastUserId: String? = null
    var lastCollectionEnabled: Boolean? = null

    override fun log(name: String, params: Map<String, Any?>) {
        events += Event(name, params)
    }

    override fun setUserId(userId: String?) {
        lastUserId = userId
    }

    override fun setCollectionEnabled(enabled: Boolean) {
        lastCollectionEnabled = enabled
    }

    fun names(): List<String> = events.map { it.name }
}

/** 앱 어디서든 부르는 진입점. Application에서 실제 구현을 꽂고, 테스트는 [FakeAnalytics]를 꽂는다. */
object AnalyticsHub : Analytics {
    @Volatile
    var delegate: Analytics = NoOpAnalytics

    override fun log(name: String, params: Map<String, Any?>) = delegate.log(name, params)
    override fun setUserId(userId: String?) = delegate.setUserId(userId)
    override fun setCollectionEnabled(enabled: Boolean) = delegate.setCollectionEnabled(enabled)
    override fun recordError(throwable: Throwable) = delegate.recordError(throwable)
    override fun setCurrentScreen(name: String) = delegate.setCurrentScreen(name)

    /** 프로세스 시작 시각(ms). Application.onCreate에서 채운다. */
    @Volatile var appStartMs: Long = 0L
    @Volatile var isFirstLaunch: Boolean = false
    private var appOpenLogged = false
    private var prevScreen: String? = null
    private var prevScreenAtMs: Long = 0L

    /** screen_view: 앞 화면(prev_screen)과 앞 화면에 머문 시간(dwell_ms)을 같이 보낸다. 첫 화면에서 app_open도 한 번 보낸다. */
    @Synchronized
    fun screen(name: String, nowMs: Long = System.currentTimeMillis()) {
        if (!appOpenLogged) {
            appOpenLogged = true
            log(
                AnalyticsEvent.APP_OPEN,
                mapOf(
                    "is_first_launch" to isFirstLaunch,
                    "first_screen_ms" to if (appStartMs > 0) (nowMs - appStartMs).coerceAtLeast(0) else 0L,
                    AnalyticsParam.SCREEN_NAME to name
                )
            )
        }
        val params = mutableMapOf<String, Any?>(AnalyticsParam.SCREEN_NAME to name)
        prevScreen?.let {
            params["prev_screen"] = it
            params["dwell_ms"] = (nowMs - prevScreenAtMs).coerceAtLeast(0)
        }
        prevScreen = name
        prevScreenAtMs = nowMs
        setCurrentScreen(name)
        log(AnalyticsEvent.SCREEN_VIEW, params)
    }

    /** 버튼 누름: `<화면>_click` + button(+placement). */
    fun click(screen: String, button: String, placement: String? = null) =
        log("${screen}_click", mapOf("button" to button, "placement" to placement))

    /** 경로의 숫자 id를 :id로 바꾼다(/api/notices/12 -> /api/notices/:id). 쿼리는 버린다. */
    fun normalizePath(path: String): String =
        path.substringBefore('?').split('/').joinToString("/") { if (it.isNotEmpty() && it.all(Char::isDigit)) ":id" else it }

    fun apiError(path: String, status: Int, kind: ApiErrorKind) = log(
        AnalyticsEvent.API_ERROR,
        mapOf("path" to normalizePath(path), "status" to status, "kind" to kind.value)
    )
}

enum class ApiErrorKind(val value: String) { OFFLINE("offline"), TIMEOUT("timeout"), SERVER("server"), CLIENT("client") }

object AnalyticsEvent {
    const val SCREEN_VIEW = "screen_view"
    const val LOGIN_START = "login_start"
    const val LOGIN_SUCCESS = "login_success"
    const val LOGIN_FAIL = "login_fail"
    const val TERMS_AGREE = "terms_agree"
    const val REGION_SELECT = "region_select"
    const val HEALTH_PERMISSION = "health_permission"
    const val RUN_SYNC = "run_sync"
    const val RANKING_TAB = "ranking_tab"
    const val MISSION_OPEN = "mission_open"
    const val NOTICE_OPEN = "notice_open"
    const val NICKNAME_CHANGE = "nickname_change"
    const val PROFILE_PHOTO_CHANGE = "profile_photo_change"
    const val PUSH_TOGGLE = "push_toggle"
    const val MARKETING_TOGGLE = "marketing_toggle"
    const val LOGOUT = "logout"
    const val WITHDRAW = "withdraw"
    const val API_ERROR = "api_error"
    const val APP_OPEN = "app_open"
}

object AnalyticsParam {
    const val SCREEN_NAME = "screen_name"
}

/** screen_view 화면 이름(계약). */
object AnalyticsScreen {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val TERMS = "terms"
    const val TERMS_DETAIL = "terms_detail"
    const val REGION = "region"
    const val HOME = "home"
    const val RANKING = "ranking"
    const val RECORDS = "records"
    const val RECORD_DETAIL = "record_detail"
    const val MISSION = "mission"
    const val NOTICE_LIST = "notice_list"
    const val NOTICE_DETAIL = "notice_detail"
    const val MYPAGE = "mypage"
    const val PROFILE_EDIT = "profile_edit"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val NOTIFICATIONS = "notifications"
    const val WITHDRAW = "withdraw"
}
