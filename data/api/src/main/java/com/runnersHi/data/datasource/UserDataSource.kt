package com.runnersHi.data.datasource

import com.runnersHi.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun getCurrentUser(): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun clearUser()
}

interface UserRemoteDataSource {
    suspend fun fetchUser(id: String): User?
    suspend fun updateUser(user: User): User
}
