package com.runnersHi.data.user

import com.runnersHi.core.network.NicknameUpdateRequest
import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.core.network.apiCallNullable
import com.runnersHi.domain.user.model.User
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(
    private val api: RunnersHiApi
) : UserRemoteDataSource {

    override suspend fun fetchCurrentUser(): User {
        val profile = apiCall { api.getMyProfile() }
        return User(
            id = "me",
            name = profile.nickname,
            profileImageUrl = profile.profileImageUrl,
            totalDistance = 0.0,
            totalRuns = 0
        )
    }

    /** 서버에 다른 사용자 조회 API가 없다. */
    override suspend fun fetchUser(id: String): User? = null

    override suspend fun updateUser(user: User): User {
        apiCallNullable { api.updateNickname(NicknameUpdateRequest(user.name)) }
        return user
    }
}
