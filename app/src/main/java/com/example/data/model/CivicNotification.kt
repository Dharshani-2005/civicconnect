package com.example.data.model

enum class NotificationType {
    GEOFENCE_ALERT,
    SLA_BREACH,
    STATUS_UPDATE,
    AI_VISION_REPORT,
    FLEET_DISPATCH,
    MUNICIPAL_BROADCAST
}

data class CivicNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedEntityId: String? = null, // Grievance ID, Geofence ID, etc.
    val isRead: Boolean = false,
    val severity: String = "NORMAL", // "CRITICAL", "HIGH", "NORMAL"
    val actionRoute: String? = null
)
