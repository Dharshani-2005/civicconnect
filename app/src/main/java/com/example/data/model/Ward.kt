package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wards")
data class Ward(
    @PrimaryKey val code: String,
    val name: String,
    val city: String,
    val representativeName: String,
    val representativeContact: String,
    val centerLat: Double,
    val centerLng: Double,
    val radiusKm: Double
)
