package com.runnersHi.domain.user

import com.runnersHi.domain.user.model.User
import com.runnersHi.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SendFeedbackUseCaseImplTest {

    private val sent = mutableListOf<String>()
    private val useCase = SendFeedbackUseCaseImpl(object : UserRepository {
        override fun getCurrentUser(): Flow<User?> = flowOf(null)
        override suspend fun getUserById(id: String): User? = null
        override suspend fun updateUser(user: User) = Unit
        override suspend fun refreshCurrentUser() = Unit
        override suspend fun clearUser() = Unit
        override suspend fun uploadProfileImage(bytes: ByteArray, contentType: String) = Unit
        override suspend fun removeProfileImage() = Unit
        override suspend fun sendFeedback(content: String) {
            sent += content
        }
        override suspend fun updatePushToken(token: String) = Unit
    })

    @Test
    fun `앞뒤 공백을 지워서 보낸다`() = runTest {
        assertTrue(useCase("  의견  ").isSuccess)
        assertEquals(listOf("의견"), sent)
    }

    @Test
    fun `빈 내용과 너무 긴 내용은 보내지 않는다`() = runTest {
        assertTrue(useCase("   ").isFailure)
        assertTrue(useCase("가".repeat(1001)).isFailure)
        assertTrue(sent.isEmpty())
    }
}
