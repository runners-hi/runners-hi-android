package com.runnersHi.data.notice

import com.runnersHi.domain.notice.model.NoticeDetail
import com.runnersHi.domain.notice.model.NoticePage
import com.runnersHi.domain.notice.repository.NoticeRepository
import javax.inject.Inject

class NoticeRepositoryImpl @Inject constructor(
    private val remoteDataSource: NoticeRemoteDataSource
) : NoticeRepository {

    override suspend fun getNotices(cursor: String?): Result<NoticePage> =
        runCatching { remoteDataSource.fetchNotices(cursor) }

    override suspend fun getNoticeDetail(id: String): Result<NoticeDetail> =
        runCatching { remoteDataSource.fetchNoticeDetail(id) }
}
