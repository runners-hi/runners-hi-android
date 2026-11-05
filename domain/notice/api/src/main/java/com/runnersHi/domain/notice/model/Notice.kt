package com.runnersHi.domain.notice.model

import java.time.LocalDate

data class Notice(
    val id: String,
    val title: String,
    val date: LocalDate
)

data class NoticeDetail(
    val id: String,
    val title: String,
    val content: String,
    val date: LocalDate
)

data class NoticePage(
    val notices: List<Notice>,
    val nextCursor: String?
)
