package com.runnersHi.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppleAuthUrlTest {

    private val redirect = "https://runners.example.com/apple/callback"

    @Test
    fun `로그인 주소는 scope 없이 fragment로 요청한다`() {
        val url = AppleAuthUrl.authorize("https://appleid.apple.com/auth/authorize", "com.run.web", redirect, "s1")

        assertTrue(url.contains("client_id=com.run.web"))
        assertTrue(url.contains("redirect_uri=https%3A%2F%2Frunners.example.com%2Fapple%2Fcallback"))
        assertTrue(url.contains("response_type=code%20id_token"))
        assertTrue(url.contains("response_mode=fragment"))
        assertTrue(!url.contains("scope="))
    }

    @Test
    fun `결과 주소에서 토큰과 코드를 읽는다`() {
        val result = AppleAuthUrl.parseRedirect("$redirect#state=s1&code=c1&id_token=a.b.c", redirect, "s1")

        val credential = (result as AppleAuthUrl.Result.Success).credential
        assertEquals("a.b.c", credential.idToken)
        assertEquals("c1", credential.authorizationCode)
    }

    @Test
    fun `다른 주소는 건드리지 않는다`() {
        assertNull(AppleAuthUrl.parseRedirect("https://appleid.apple.com/auth", redirect, "s1"))
        assertNull(AppleAuthUrl.parseRedirect("$redirect/other#code=1", redirect, "s1"))
    }

    @Test
    fun `state가 다르면 거절한다`() {
        val result = AppleAuthUrl.parseRedirect("$redirect#state=x&code=c&id_token=t", redirect, "s1")

        assertTrue(result is AppleAuthUrl.Result.Failure)
    }

    @Test
    fun `사용자가 취소하면 취소로 처리한다`() {
        val result = AppleAuthUrl.parseRedirect("$redirect#state=s1&error=user_cancelled_authorize", redirect, "s1")

        assertEquals(AppleAuthUrl.Result.Cancelled, result)
    }
}
