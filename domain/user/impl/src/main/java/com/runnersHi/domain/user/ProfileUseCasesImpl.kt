package com.runnersHi.domain.user

import com.runnersHi.domain.user.repository.UserRepository
import com.runnersHi.domain.user.usecase.RemoveProfileImageUseCase
import com.runnersHi.domain.user.usecase.SendFeedbackUseCase
import com.runnersHi.domain.user.usecase.UpdateProfileImageUseCase
import com.runnersHi.domain.user.usecase.UpdatePushTokenUseCase
import javax.inject.Inject

class UpdateProfileImageUseCaseImpl @Inject constructor(
    private val repository: UserRepository
) : UpdateProfileImageUseCase {
    override suspend fun invoke(jpegBytes: ByteArray): Result<Unit> =
        runCatching { repository.uploadProfileImage(jpegBytes, "image/jpeg") }
}

class RemoveProfileImageUseCaseImpl @Inject constructor(
    private val repository: UserRepository
) : RemoveProfileImageUseCase {
    override suspend fun invoke(): Result<Unit> = runCatching { repository.removeProfileImage() }
}

class SendFeedbackUseCaseImpl @Inject constructor(
    private val repository: UserRepository
) : SendFeedbackUseCase {
    override suspend fun invoke(content: String): Result<Unit> = runCatching {
        val trimmed = content.trim()
        require(trimmed.isNotEmpty()) { "내용을 입력해 주세요" }
        require(trimmed.length <= SendFeedbackUseCase.MAX_LENGTH) {
            "${SendFeedbackUseCase.MAX_LENGTH}자까지 보낼 수 있어요"
        }
        repository.sendFeedback(trimmed)
    }
}

class UpdatePushTokenUseCaseImpl @Inject constructor(
    private val repository: UserRepository
) : UpdatePushTokenUseCase {
    override suspend fun invoke(token: String): Result<Unit> =
        runCatching { repository.updatePushToken(token) }
}
