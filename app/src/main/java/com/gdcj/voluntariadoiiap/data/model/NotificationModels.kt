package com.gdcj.voluntariadoiiap.data.model

import java.util.Date

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.GENERAL
)

enum class NotificationType {
    GENERAL,
    PROJECT_UPDATE,
    ACHIEVEMENT_UNLOCKED,
    APPLICATION_STATUS
}
