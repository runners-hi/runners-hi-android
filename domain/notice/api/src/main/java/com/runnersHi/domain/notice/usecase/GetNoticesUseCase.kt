package com.runnersHi.domain.notice.usecase

import com.runnersHi.domain.notice.model.NoticePage

interface GetNoticesUseCase {
    suspend operator fun invoke(cursor: String?): Result<NoticePage>
}
