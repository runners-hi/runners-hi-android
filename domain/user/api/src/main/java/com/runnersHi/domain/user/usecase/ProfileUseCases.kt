package com.runnersHi.domain.user.usecase

interface UpdateProfileImageUseCase {
    suspend operator fun invoke(jpegBytes: ByteArray): Result<Unit>
}

interface RemoveProfileImageUseCase {
    suspend operator fun invoke(): Result<Unit>
}

interface SendFeedbackUseCase {
    suspend operator fun invoke(content: String): Result<Unit>

    companion object {
        const val MAX_LENGTH = 1000
    }
}

interface UpdatePushTokenUseCase {
    suspend operator fun invoke(token: String): Result<Unit>
}
