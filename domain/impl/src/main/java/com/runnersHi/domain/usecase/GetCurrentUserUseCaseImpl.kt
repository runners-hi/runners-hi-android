package com.runnersHi.domain.usecase

import com.runnersHi.domain.model.User
import com.runnersHi.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : GetCurrentUserUseCase {

    override fun invoke(): Flow<User?> {
        return userRepository.getCurrentUser()
    }
}
