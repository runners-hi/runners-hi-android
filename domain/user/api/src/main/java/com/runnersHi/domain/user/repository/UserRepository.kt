package com.runnersHi.domain.user.repository

import com.runnersHi.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun getUserById(id: String): User?
    suspend fun updateUser(user: User)
    suspend fun refreshCurrentUser()
    suspend fun clearUser()
    suspend fun uploadProfileImage(bytes: ByteArray, contentType: String)
    suspend fun removeProfileImage()
    suspend fun sendFeedback(content: String)
    suspend fun updatePushToken(token: String)
}
