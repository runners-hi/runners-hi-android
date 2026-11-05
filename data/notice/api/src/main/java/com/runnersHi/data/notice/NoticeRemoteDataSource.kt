package com.runnersHi.data.notice

import com.runnersHi.domain.notice.model.NoticeDetail
import com.runnersHi.domain.notice.model.NoticePage

interface NoticeRemoteDataSource {
    suspend fun fetchNotices(cursor: String?): NoticePage
    suspend fun fetchNoticeDetail(id: String): NoticeDetail
}
