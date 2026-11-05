package com.runnersHi.core.network

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class NetworkFactoryTest {

    private class MemoryStore(var access: String? = "old", var refresh: String? = "r1") : TokenStore {
        override fun accessToken() = access
        override fun refreshToken() = refresh
        override fun save(accessToken: String, refreshToken: String) {
            access = accessToken
            refresh = refreshToken
        }
        override fun clear() {
            access = null
            refresh = null
        }
    }

    private val server = MockWebServer()
    private val store = MemoryStore()
    private lateinit var api: RunnersHiApi

    private val profile = """{"success":true,"data":{"nickname":"러너","unknown":1},"error":null}"""

    @Before
    fun setUp() {
        server.start()
        api = NetworkFactory.createApi(server.url("/").toString(), store)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `요청에 액세스 토큰을 붙이고 모르는 필드는 무시한다`() = runTest {
        server.enqueue(MockResponse().setBody(profile))

        assertEquals("러너", apiCall { api.getMyProfile() }.nickname)
        assertEquals("Bearer old", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `로그인 요청에는 토큰을 붙이지 않는다`() = runTest {
        server.enqueue(MockResponse().setBody("""{"success":true,"data":{"accessToken":"a","refreshToken":"b","isNewUser":true}}"""))

        assertTrue(apiCall { api.loginWithKakao(KakaoLoginRequest("k")) }.isNewUser)
        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `버전 확인 요청에는 토큰을 붙이지 않는다`() = runTest {
        server.enqueue(MockResponse().setBody("""{"success":true,"data":{"needsUpdate":false}}"""))

        runCatching { api.checkVersion("ANDROID", "1.0.0") }

        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `401이면 토큰을 갱신하고 다시 보낸다`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setBody("""{"success":true,"data":{"accessToken":"new","refreshToken":"r2"}}"""))
        server.enqueue(MockResponse().setBody(profile))

        assertEquals("러너", apiCall { api.getMyProfile() }.nickname)

        server.takeRequest()
        assertEquals("/api/auth/refresh", server.takeRequest().path)
        assertEquals("Bearer new", server.takeRequest().getHeader("Authorization"))
        assertEquals("new", store.access)
        assertEquals("r2", store.refresh)
    }

    @Test
    fun `갱신이 거절되면 토큰을 지우고 세션 만료를 알린다`() = runTest(UnconfinedTestDispatcher()) {
        var expired = false
        val job = launch { SessionEvents.expired.first(); expired = true }
        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(401))

        try {
            apiCall { api.getMyProfile() }
            fail("실패해야 한다")
        } catch (e: ApiException) {
            assertEquals(401, e.httpStatus)
        }

        assertNull(store.access)
        assertTrue(expired)
        job.cancel()
    }

    @Test
    fun `서버 오류 문구를 그대로 전달한다`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(400)
                .setBody("""{"success":false,"data":null,"error":{"code":"U005","message":"지역을 먼저 선택해주세요"}}""")
        )

        try {
            apiCall { api.getMyRanking() }
            fail("실패해야 한다")
        } catch (e: ApiException) {
            assertEquals("U005", e.code)
            assertEquals("지역을 먼저 선택해주세요", e.message)
        }
    }

    @Test
    fun `빈 객체 응답도 성공으로 처리한다`() = runTest {
        server.enqueue(MockResponse().setBody("""{"success":true,"data":{},"error":null}"""))

        val data = apiCall { api.logout() }

        assertEquals("{}", data.toString())
    }

    @Test
    fun `연결이 끊기면 네트워크 오류로 바꾼다`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        try {
            apiCall { api.getRegions() }
            fail("실패해야 한다")
        } catch (e: NetworkException) {
            assertEquals("서버에 연결할 수 없어요", e.message)
        }
    }
}
