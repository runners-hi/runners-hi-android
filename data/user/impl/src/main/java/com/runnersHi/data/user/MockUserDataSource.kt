package com.runnersHi.data.user

import com.runnersHi.domain.user.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockUserRemoteDataSource @Inject constructor() : UserRemoteDataSource {

    override suspend fun fetchCurrentUser(): User = User(
        id = "me",
        name = "잘먹는프로도",
        profileImageUrl = null,
        totalDistance = 0.0,
        totalRuns = 0
    )

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

    override suspend fun uploadProfileImage(bytes: ByteArray, contentType: String) {
        throw IllegalStateException("서버에 연결되면 쓸 수 있어요")
    }

    override suspend fun removeProfileImage() = Unit

    override suspend fun sendFeedback(content: String) = Unit

    override suspend fun updatePushToken(token: String) = Unit
}
