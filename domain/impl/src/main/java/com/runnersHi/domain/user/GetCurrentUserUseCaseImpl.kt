package com.runnersHi.domain.user

import com.runnersHi.domain.user.model.User
import com.runnersHi.domain.user.repository.UserRepository
import com.runnersHi.domain.user.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : GetCurrentUserUseCase {

    override fun invoke(): Flow<User?> {
        return userRepository.getCurrentUser()
    }
}
