package com.runnersHi.data.user

import com.runnersHi.core.common.analytics.AnalyticsHub
import com.runnersHi.domain.user.model.User
import com.runnersHi.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun getCurrentUser(): Flow<User?> {
        return localDataSource.getCurrentUser()
    }

    override suspend fun getUserById(id: String): User? {
        return remoteDataSource.fetchUser(id)
    }

    override suspend fun refreshCurrentUser() {
        val user = remoteDataSource.fetchCurrentUser()
        localDataSource.saveUser(user)
        // user_id는 서버 회원 숫자 id만 쓴다(이메일·닉네임 금지).
        AnalyticsHub.setUserId(user.id.takeIf { it.isNotEmpty() && it.all(Char::isDigit) })
    }

    override suspend fun uploadProfileImage(bytes: ByteArray, contentType: String) {
        remoteDataSource.uploadProfileImage(bytes, contentType)
        refreshCurrentUser()
    }

    override suspend fun removeProfileImage() {
        remoteDataSource.removeProfileImage()
        refreshCurrentUser()
    }

    override suspend fun sendFeedback(content: String) = remoteDataSource.sendFeedback(content)

    override suspend fun updatePushToken(token: String) = remoteDataSource.updatePushToken(token)

    override suspend fun clearUser() {
        localDataSource.clearUser()
        AnalyticsHub.setUserId(null)
    }

    override suspend fun updateUser(user: User) {
        val updatedUser = remoteDataSource.updateUser(user)
        localDataSource.saveUser(updatedUser)
    }
}
