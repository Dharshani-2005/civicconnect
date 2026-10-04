package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actionType: String, // LOGIN, STATUS_CHANGE, ANONYMOUS_PETITION, SLA_ESCALATION, ACCESS_GRANT
    val userId: String,
    val details: String,
    val ipHash: String = "127.0.0.1",
    val timestamp: String
)
