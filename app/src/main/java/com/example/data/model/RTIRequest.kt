package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rti_requests")
data class RTIRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val citizenId: Int,
    val citizenName: String,
    val subject: String,
    val department: String,
    val description: String,
    val submittedAt: String,
    val deadline: String,
    val status: String = "SUBMITTED", // DRAFT, SUBMITTED, RESPONDED, EXPIRED
    val pdfPath: String? = null
)
