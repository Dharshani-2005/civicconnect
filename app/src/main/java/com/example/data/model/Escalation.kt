package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "escalations")
data class Escalation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val grievanceId: Int,
    val escalatedTo: String, // OFFICER, DEPARTMENT_HEAD, COMMISSIONER
    val reason: String, // SLA_BREACH, CITIZEN_APPEAL, HIGH_PRIORITY
    val escalatedAt: String,
    val resolved: Boolean = false
)
