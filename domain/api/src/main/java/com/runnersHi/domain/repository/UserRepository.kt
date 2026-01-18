package com.runnersHi.domain.repository

import com.runnersHi.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun getUserById(id: String): User?
    suspend fun updateUser(user: User)
}
