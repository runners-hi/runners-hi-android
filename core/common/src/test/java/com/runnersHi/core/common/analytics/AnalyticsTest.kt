package com.runnersHi.core.common.analytics

import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyticsTest {
    @Test
    fun 경로의_숫자_id는_치환된다() {
        assertEquals("/api/notices/:id", AnalyticsHub.normalizePath("/api/notices/12"))
        assertEquals("/api/missions/:id/claim", AnalyticsHub.normalizePath("/api/missions/7/claim?x=1"))
        assertEquals("/api/ranking", AnalyticsHub.normalizePath("/api/ranking"))
    }

    @Test
    fun 가짜_구현이_이벤트를_모은다() {
        val fake = FakeAnalytics()
        AnalyticsHub.delegate = fake
        try {
            AnalyticsHub.screen(AnalyticsScreen.HOME)
            AnalyticsHub.apiError("/api/ranking", 500, ApiErrorKind.SERVER)
        } finally {
            AnalyticsHub.delegate = NoOpAnalytics
        }
        assertEquals(listOf("screen_view", "api_error"), fake.names())
        assertEquals("home", fake.events[0].params["screen_name"])
        assertEquals("server", fake.events[1].params["kind"])
    }
}
