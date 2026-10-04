package com.example.service

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.data.model.CivicNotification
import com.example.data.model.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object NotificationService {

    private val initialNotifications = listOf(
        CivicNotification(
            id = "NOTIF-01",
            title = "⚠️ Entered Active Geo-Fence Zone",
            message = "You are near Anna Nagar East Stormwater Drain Excavation. Heavy machinery active; diversion via 3rd Avenue.",
            type = NotificationType.GEOFENCE_ALERT,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
            severity = "HIGH",
            actionRoute = "map"
        ),
        CivicNotification(
            id = "NOTIF-02",
            title = "🛠️ Officer Assigned: Grievance #GRV-2025-4819",
            message = "Junior Engineer S. Ramanathan has been assigned to your pothole complaint. Inspection scheduled within 4 hours.",
            type = NotificationType.STATUS_UPDATE,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
            relatedEntityId = "GRV-2025-4819",
            severity = "NORMAL",
            actionRoute = "grievances"
        ),
        CivicNotification(
            id = "NOTIF-03",
            title = "🤖 AI Computer Vision Inspection Completed",
            message = "AI classified damage with 88% Critical Severity. Fast-track 24h SLA dispatched to Highways Division.",
            type = NotificationType.AI_VISION_REPORT,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 90,
            severity = "CRITICAL",
            actionRoute = "ai_vision"
        ),
        CivicNotification(
            id = "NOTIF-04",
            title = "🚨 SLA Escalation to Executive Engineer",
            message = "Grievance #GRV-2025-3921 exceeded 48h resolution SLA and has been auto-escalated to Level-2 Commissioner review.",
            type = NotificationType.SLA_BREACH,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
            severity = "CRITICAL",
            actionRoute = "admin"
        ),
        CivicNotification(
            id = "NOTIF-05",
            title = "📢 Monsoon Drainage Desilting Advisory",
            message = "Greater Chennai Corporation (GCC) Special Desilting Drive active across Wards 01-05. Report blocked inlets via app.",
            type = NotificationType.MUNICIPAL_BROADCAST,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 360,
            severity = "NORMAL",
            actionRoute = "dashboard"
        )
    )

    private val _notifications = MutableStateFlow<List<CivicNotification>>(initialNotifications)
    val notifications: StateFlow<List<CivicNotification>> = _notifications.asStateFlow()

    // Active Heads-up Push Toast Notification
    private val _currentPushBanner = MutableStateFlow<CivicNotification?>(null)
    val currentPushBanner: StateFlow<CivicNotification?> = _currentPushBanner.asStateFlow()

    fun postNotification(
        title: String,
        message: String,
        type: NotificationType,
        severity: String = "NORMAL",
        relatedEntityId: String? = null,
        actionRoute: String? = null
    ) {
        val notif = CivicNotification(
            id = "NOTIF-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis(),
            severity = severity,
            relatedEntityId = relatedEntityId,
            actionRoute = actionRoute
        )
        _notifications.value = listOf(notif) + _notifications.value
        _currentPushBanner.value = notif
    }

    fun dismissPushBanner() {
        _currentPushBanner.value = null
    }

    fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun sendSmsOtpNotification(context: Context, phone: String, otpCode: String) {
        try {
            // Also try sending via telephony SmsManager if available and permitted
            try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(android.telephony.SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    android.telephony.SmsManager.getDefault()
                }
                smsManager?.sendTextMessage(
                    phone,
                    null,
                    "Your TN CM Helpline verification OTP is $otpCode. Valid for 10 minutes. - Govt of Tamil Nadu",
                    null,
                    null
                )
            } catch (ignored: Exception) {
                // Device might not have a cellular SIM card or SMS permission; fallback to system notification
            }

            // Post real Android System Status Bar Notification (SMS Notification)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (notificationManager != null) {
                val channelId = "sms_otp_channel"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "SMS Messages & OTPs",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Incoming SMS verification codes"
                        enableVibration(true)
                        enableLights(true)
                    }
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.sym_action_chat)
                    .setContentTitle("💬 Messages • 1100-TNGOVT")
                    .setContentText("Your TN CM Helpline verification code is $otpCode. Do not share.")
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("Your TN CM Helpline verification code is $otpCode. Valid for 10 minutes. Do not share this OTP with anyone. - Govt of Tamil Nadu"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(1100, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
