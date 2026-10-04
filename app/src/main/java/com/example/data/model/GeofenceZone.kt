package com.example.data.model

data class GeofenceZone(
    val id: String,
    val name: String,
    val wardCode: String,
    val wardName: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Double,
    val hazardType: String, // "ROAD_WORK", "WATER_LOGGING", "PIPELINE_BURST", "ELECTRICAL_MAINTENANCE", "SANITATION_DRIVE"
    val severity: String, // "CRITICAL", "HIGH", "MODERATE", "ADVISORY"
    val description: String,
    val diversionRoute: String? = null,
    val activeUntil: String,
    val isCurrentlyActive: Boolean = true
)
