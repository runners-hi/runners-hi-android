package com.runnersHi.data.notice

import com.runnersHi.core.network.RunnersHiApi
import com.runnersHi.core.network.apiCall
import com.runnersHi.domain.notice.model.Notice
import com.runnersHi.domain.notice.model.NoticeDetail
import com.runnersHi.domain.notice.model.NoticePage
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class RemoteNoticeDataSource @Inject constructor(
    private val api: RunnersHiApi
) : NoticeRemoteDataSource {

    override suspend fun fetchNotices(cursor: String?): NoticePage {
        val page = apiCall { api.getNotices(cursor?.toLong(), PAGE_SIZE) }
        return NoticePage(
            notices = page.notices.map { Notice(it.id.toString(), it.title, it.createdAt.toDate()) },
            nextCursor = page.nextCursor?.takeIf { page.hasNext }?.toString()
        )
    }

    override suspend fun fetchNoticeDetail(id: String): NoticeDetail {
        val notice = apiCall { api.getNoticeDetail(id.toLong()) }
        return NoticeDetail(notice.id.toString(), notice.title, notice.content, notice.createdAt.toDate())
    }

    private fun String.toDate(): LocalDate = LocalDateTime.parse(this).toLocalDate()

    private companion object {
        const val PAGE_SIZE = 20
    }
}

/** 서버 주소가 없을 때: 공지 내용을 지어내지 않고 빈 목록을 준다. */
class EmptyNoticeDataSource @Inject constructor() : NoticeRemoteDataSource {
    override suspend fun fetchNotices(cursor: String?) = NoticePage(emptyList(), null)
    override suspend fun fetchNoticeDetail(id: String): NoticeDetail =
        throw NoSuchElementException("공지를 찾을 수 없어요")
}
