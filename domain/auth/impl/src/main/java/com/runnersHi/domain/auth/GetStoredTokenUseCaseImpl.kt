package com.runnersHi.domain.auth

import com.runnersHi.domain.auth.model.AuthToken
import com.runnersHi.domain.auth.repository.AuthRepository
import com.runnersHi.domain.auth.usecase.GetStoredTokenUseCase
import javax.inject.Inject

class GetStoredTokenUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : GetStoredTokenUseCase {

    override suspend fun invoke(): AuthToken? = authRepository.getStoredToken()
}
