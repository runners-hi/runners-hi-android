package com.runnersHi.data.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.model.SocialLoginType
import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.LoginResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val localDataSource: AuthLocalDataSource,
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun getStoredToken(): AuthToken? {
        return localDataSource.getToken()
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthToken> {
        return try {
            val newToken = remoteDataSource.refreshToken(refreshToken)
            localDataSource.saveToken(newToken)
            Result.success(newToken)
        } catch (e: TokenExpiredException) {
            localDataSource.clearToken()
            Result.failure(e)
        } catch (e: Exception) {
            // 네트워크 오류로는 로그인을 풀지 않는다.
            Result.failure(e)
        }
    }

    override suspend fun saveToken(token: AuthToken) {
        localDataSource.saveToken(token)
    }

    override suspend fun clearToken() {
        localDataSource.clearToken()
    }

    override suspend fun logoutFromServer() {
        if (localDataSource.hasToken()) remoteDataSource.logout()
    }

    override suspend fun withdraw(): Result<Unit> = runCatching { remoteDataSource.withdraw() }

    override fun isLoggedIn(): Boolean {
        return localDataSource.hasToken()
    }

    override suspend fun loginWithSocial(type: SocialLoginType, token: String): Result<LoginResult> {
        return try {
            val response = remoteDataSource.loginWithSocial(type, token)
            response.token?.let { localDataSource.saveToken(it) }
            Result.success(response.result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
