package com.runnersHi.domain.notice.usecase

import com.runnersHi.domain.notice.model.NoticeDetail

interface GetNoticeDetailUseCase {
    suspend operator fun invoke(id: String): Result<NoticeDetail>
}
