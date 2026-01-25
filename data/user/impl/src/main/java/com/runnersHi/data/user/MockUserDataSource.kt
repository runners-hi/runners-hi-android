package com.runnersHi.data.user

import com.runnersHi.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockUserLocalDataSource @Inject constructor() : UserLocalDataSource {

    private val currentUser = MutableStateFlow<User?>(
        User(
            id = "0",
            name = "테스트유저",
            profileImageUrl = null,
            totalDistance = 45.2,
            totalRuns = 12
        )
    )

    override fun getCurrentUser(): Flow<User?> = currentUser

    override suspend fun saveUser(user: User) {
        currentUser.value = user
    }

    override suspend fun clearUser() {
        currentUser.value = null
    }
}

@Singleton
class MockUserRemoteDataSource @Inject constructor() : UserRemoteDataSource {

    override suspend fun fetchUser(id: String): User? {
        return User(
            id = id,
            name = "User $id",
            profileImageUrl = null,
            totalDistance = 0.0,
            totalRuns = 0
        )
    }

    override suspend fun updateUser(user: User): User {
        return user
    }
}
