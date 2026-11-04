package com.runnersHi.domain.auth.usecase

import com.runnersHi.domain.auth.model.AuthToken

interface GetStoredTokenUseCase {
    suspend operator fun invoke(): AuthToken?
}
