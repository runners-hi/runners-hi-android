package com.runnersHi.domain.notice

import com.runnersHi.domain.notice.model.NoticeDetail
import com.runnersHi.domain.notice.model.NoticePage
import com.runnersHi.domain.notice.repository.NoticeRepository
import com.runnersHi.domain.notice.usecase.GetNoticeDetailUseCase
import com.runnersHi.domain.notice.usecase.GetNoticesUseCase
import javax.inject.Inject

class GetNoticesUseCaseImpl @Inject constructor(
    private val repository: NoticeRepository
) : GetNoticesUseCase {
    override suspend fun invoke(cursor: String?): Result<NoticePage> = repository.getNotices(cursor)
}

class GetNoticeDetailUseCaseImpl @Inject constructor(
    private val repository: NoticeRepository
) : GetNoticeDetailUseCase {
    override suspend fun invoke(id: String): Result<NoticeDetail> = repository.getNoticeDetail(id)
}
