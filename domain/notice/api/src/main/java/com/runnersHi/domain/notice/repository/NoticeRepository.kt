package com.runnersHi.domain.notice.repository

import com.runnersHi.domain.notice.model.NoticeDetail
import com.runnersHi.domain.notice.model.NoticePage

interface NoticeRepository {
    suspend fun getNotices(cursor: String?): Result<NoticePage>
    suspend fun getNoticeDetail(id: String): Result<NoticeDetail>
}
