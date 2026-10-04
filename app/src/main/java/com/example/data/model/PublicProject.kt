package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "public_projects")
data class PublicProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val department: String,
    val wardCode: String,
    val description: String,
    val startDate: String,
    val expectedEndDate: String,
    val budgetAllocated: Double,
    val budgetSpent: Double,
    val status: String // PLANNED, ONGOING, COMPLETED, STALLED
)
