package com.example.data.model

data class FleetVehicle(
    val id: String,
    val vehicleNumber: String,
    val type: String, // "SUCTION_JETTING", "ROAD_PATCH_VAN", "ELECTRICAL_CRANE", "GARBAGE_COMPACTOR", "AMBULANCE_REPAIR"
    val vehicleName: String,
    val driverName: String,
    val driverPhone: String,
    val currentLat: Double,
    val currentLng: Double,
    val headingDegrees: Float,
    val speedKmh: Int,
    val status: String, // "EN_ROUTE_TO_SITE", "ON_SITE_REPAIRING", "RETURNING_TO_DEPOT", "STANDBY"
    val assignedGrievanceId: String? = null,
    val assignedGrievanceTitle: String? = null,
    val destinationWard: String,
    val etaMinutes: Int
)
