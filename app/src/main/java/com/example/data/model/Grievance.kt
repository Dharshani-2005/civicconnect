package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grievances")
data class Grievance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackingId: String,
    val title: String,
    val description: String,
    val category: String, // ROAD, WATER, SANITATION, ELECTRICITY, OTHER
    val latitude: Double,
    val longitude: Double,
    val geohash: String,
    val status: String, // SUBMITTED, IN_PROGRESS, RESOLVED, CLOSED, ESCALATED
    val priorityScore: Int,
    val anonymous: Boolean,
    val photoPath: String? = null,
    val submittedAt: String,
    val resolvedAt: String? = null,
    val slaDeadline: String,
    val upvoteCount: Int = 0,
    val citizenId: Int,
    val wardCode: String,
    val officerNote: String? = null
) {
    companion object {
        fun getSLADays(category: String): Int = when (category.uppercase()) {
            "ELECTRICITY" -> 2
            "WATER" -> 3
            "SANITATION" -> 5
            "ROAD" -> 7
            else -> 10
        }

        fun getBasePriority(category: String): Int = when (category.uppercase()) {
            "ELECTRICITY" -> 80
            "WATER" -> 75
            "SANITATION" -> 65
            "ROAD" -> 60
            else -> 40
        }
    }
}
