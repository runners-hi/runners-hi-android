package com.runnersHi.domain.notification.model

import java.time.LocalDateTime

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)
