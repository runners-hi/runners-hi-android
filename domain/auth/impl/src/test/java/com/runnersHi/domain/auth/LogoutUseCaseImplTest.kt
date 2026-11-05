package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.LoginResult
import com.runnersHi.domain.region.model.Region
import com.runnersHi.domain.region.repository.RegionRepository
import com.runnersHi.domain.terms.model.TermsItem
import com.runnersHi.domain.terms.repository.TermsRepository
import com.runnersHi.domain.user.model.User
import com.runnersHi.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LogoutUseCaseImplTest {

    private val cleared = mutableListOf<String>()
    private var serverFails = false

    private val auth = object : AuthRepository {
        override suspend fun getStoredToken(): AuthToken? = null
        override suspend fun refreshToken(refreshToken: String) = Result.failure<AuthToken>(UnsupportedOperationException())
        override suspend fun saveToken(token: AuthToken) = Unit
        override suspend fun clearToken() { cleared += "token" }
        override suspend fun logoutFromServer() {
            cleared += "server"
            if (serverFails) error("network")
        }
        override fun isLoggedIn() = false
        override suspend fun loginWithSocial(type: SocialLoginType, token: String) =
            Result.failure<LoginResult>(UnsupportedOperationException())
    }
    private val user = object : UserRepository {
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override suspend fun getUserById(id: String): User? = null
        override suspend fun updateUser(user: User) = Unit
        override suspend fun refreshCurrentUser() = Unit
        override suspend fun clearUser() { cleared += "user" }
    }
    private val region = object : RegionRepository {
        override suspend fun searchRegions(query: String) = Result.success(emptyList<Region>())
        override suspend fun selectRegion(regionId: String, regionName: String) = Result.success(Unit)
        override fun observeSelectedRegion(): Flow<Region?> = flowOf(null)
        override suspend fun clearSelectedRegion() { cleared += "region" }
    }
    private val terms = object : TermsRepository {
        override suspend fun getTermsList() = Result.success(emptyList<TermsItem>())
        override suspend fun agreeTerms(agreedTermIds: List<String>) = Result.success(Unit)
        override suspend fun hasAgreedTerms() = false
        override suspend fun clearAgreement() { cleared += "terms" }
    }

    @Test
    fun `로그아웃하면 토큰과 사용자, 지역, 약관 동의를 모두 지운다`() = runTest {
        LogoutUseCaseImpl(auth, user, region, terms).invoke()

        assertEquals(setOf("server", "token", "user", "region", "terms"), cleared.toSet())
    }

    @Test
    fun `서버 로그아웃이 실패해도 기기에서는 로그아웃한다`() = runTest {
        serverFails = true

        LogoutUseCaseImpl(auth, user, region, terms).invoke()

        assertEquals(setOf("server", "token", "user", "region", "terms"), cleared.toSet())
    }
}
