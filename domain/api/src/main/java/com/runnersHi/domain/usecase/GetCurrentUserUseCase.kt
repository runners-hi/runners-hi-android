package com.runnersHi.domain.usecase

import com.runnersHi.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GetCurrentUserUseCase {
    operator fun invoke(): Flow<User?>
}
