package com.runnersHi.domain.user.usecase

import com.runnersHi.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface GetCurrentUserUseCase {
    operator fun invoke(): Flow<User?>
}
