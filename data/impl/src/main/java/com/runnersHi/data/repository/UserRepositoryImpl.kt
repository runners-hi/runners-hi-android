package com.runnersHi.data.repository

import com.runnersHi.data.datasource.UserLocalDataSource
import com.runnersHi.data.datasource.UserRemoteDataSource
import com.runnersHi.domain.model.User
import com.runnersHi.domain.repository.UserRepository
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

    override suspend fun updateUser(user: User) {
        val updatedUser = remoteDataSource.updateUser(user)
        localDataSource.saveUser(updatedUser)
    }
}
