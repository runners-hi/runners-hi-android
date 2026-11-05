package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.usecase.LoginResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {

    private val token = AuthToken("access", "refresh", 3600)

    private class FakeLocal : AuthLocalDataSource {
        var stored: AuthToken? = null
        override suspend fun getToken() = stored
        override suspend fun saveToken(token: AuthToken) { stored = token }
        override suspend fun clearToken() { stored = null }
        override fun hasToken() = stored != null
    }

    private class FakeRemote(
        private val refreshError: Exception = TokenExpiredException("expired"),
        private val response: () -> SocialLoginResponse
    ) : AuthRemoteDataSource {
        override suspend fun refreshToken(refreshToken: String) = throw refreshError
        override suspend fun loginWithSocial(type: SocialLoginType, token: String) = response()
    }

    @Test
    fun `소셜 로그인에 성공하면 토큰을 저장한다`() = runTest {
        val local = FakeLocal()
        val repository = AuthRepositoryImpl(
            local,
            FakeRemote { SocialLoginResponse(LoginResult.NewUser("u1"), token) }
        )

        val result = repository.loginWithSocial(SocialLoginType.KAKAO, "kakao")

        assertEquals(LoginResult.NewUser("u1"), result.getOrNull())
        assertEquals(token, local.stored)
        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun `소셜 로그인에 실패하면 토큰을 저장하지 않는다`() = runTest {
        val local = FakeLocal()
        val repository = AuthRepositoryImpl(local, FakeRemote { error("network") })

        val result = repository.loginWithSocial(SocialLoginType.KAKAO, "kakao")

        assertTrue(result.isFailure)
        assertNull(local.stored)
        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `토큰 갱신에 실패하면 저장된 토큰을 지운다`() = runTest {
        val local = FakeLocal().apply { stored = token }
        val repository = AuthRepositoryImpl(local, FakeRemote { error("unused") })

        val result = repository.refreshToken("refresh")

        assertTrue(result.isFailure)
        assertNull(local.stored)
    }

    @Test
    fun `네트워크 오류로 갱신에 실패하면 토큰을 유지한다`() = runTest {
        val local = FakeLocal().apply { stored = token }
        val repository = AuthRepositoryImpl(local, FakeRemote(java.io.IOException("offline")) { error("unused") })

        val result = repository.refreshToken("refresh")

        assertTrue(result.isFailure)
        assertEquals(token, local.stored)
    }
}
