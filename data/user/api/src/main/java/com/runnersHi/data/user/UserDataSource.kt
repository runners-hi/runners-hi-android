package com.runnersHi.data.user

import com.runnersHi.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun getCurrentUser(): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun clearUser()
}

interface UserRemoteDataSource {
    suspend fun fetchCurrentUser(): User
    suspend fun fetchUser(id: String): User?
    suspend fun updateUser(user: User): User
    suspend fun uploadProfileImage(bytes: ByteArray, contentType: String)
    suspend fun removeProfileImage()
    suspend fun sendFeedback(content: String)
    suspend fun updatePushToken(token: String)
}
