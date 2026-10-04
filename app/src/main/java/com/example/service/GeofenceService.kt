package com.example.service

import com.example.data.model.GeofenceZone
import kotlin.math.*

object GeofenceService {

    val activeMunicipalZones: List<GeofenceZone> = listOf(
        GeofenceZone(
            id = "ZONE-01",
            name = "Anna Nagar East - Stormwater Drain Excavation",
            wardCode = "W001",
            wardName = "Anna Nagar",
            latitude = 13.0850,
            longitude = 80.2150,
            radiusMeters = 150.0,
            hazardType = "ROAD_WORK",
            severity = "HIGH",
            description = "Active heavy excavation for RCC stormwater drain connection. Heavy machinery present.",
            diversionRoute = "Via 3rd Avenue Main Road",
            activeUntil = "Today, 8:00 PM"
        ),
        GeofenceZone(
            id = "ZONE-02",
            name = "T. Nagar Ranganathan St - Pedestrian Smart Plaza Paving",
            wardCode = "W002",
            wardName = "T. Nagar",
            latitude = 13.0418,
            longitude = 80.2341,
            radiusMeters = 100.0,
            hazardType = "ROAD_WORK",
            severity = "MODERATE",
            description = "Paver block laying and utility trenching. Restricted vehicular access.",
            diversionRoute = "Via Usman Road Flyover",
            activeUntil = "Tomorrow, 6:00 AM"
        ),
        GeofenceZone(
            id = "ZONE-03",
            name = "Guindy Kathipara - Underground HT Cable Laying",
            wardCode = "W004",
            wardName = "Guindy",
            latitude = 13.0067,
            longitude = 80.2025,
            radiusMeters = 180.0,
            hazardType = "ELECTRICAL_MAINTENANCE",
            severity = "CRITICAL",
            description = "High-voltage 33kV line shifting near rotary junction. High caution advised.",
            diversionRoute = "Via GST Service Lane",
            activeUntil = "Tonight, 11:30 PM"
        ),
        GeofenceZone(
            id = "ZONE-04",
            name = "Velachery 100ft Road - Low-Lying Waterlogging Zone",
            wardCode = "W005",
            wardName = "Velachery",
            latitude = 12.9759,
            longitude = 80.2209,
            radiusMeters = 220.0,
            hazardType = "WATER_LOGGING",
            severity = "CRITICAL",
            description = "Monsoon drainage desilting in progress. Minor water accumulation on left corridor.",
            diversionRoute = "Via Bypass Link Road",
            activeUntil = "Active 24x7"
        ),
        GeofenceZone(
            id = "ZONE-05",
            name = "Mylapore Luz Church Road - Sewer Line Replacement",
            wardCode = "W003",
            wardName = "Mylapore",
            latitude = 13.0335,
            longitude = 80.2675,
            radiusMeters = 120.0,
            hazardType = "PIPELINE_BURST",
            severity = "HIGH",
            description = "Deep sewage pumping pipe replacement. One-way traffic enforced.",
            diversionRoute = "Via Kutchery Road",
            activeUntil = "Tomorrow, 12:00 PM"
        )
    )

    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Checks if given coordinates are within any active geo-fence zone.
     * Returns list of zones the user is currently inside, along with distance in meters.
     */
    fun checkGeofences(userLat: Double, userLng: Double): List<Pair<GeofenceZone, Double>> {
        val insideZones = mutableListOf<Pair<GeofenceZone, Double>>()
        for (zone in activeMunicipalZones) {
            if (zone.isCurrentlyActive) {
                val distance = calculateDistanceMeters(userLat, userLng, zone.latitude, zone.longitude)
                if (distance <= zone.radiusMeters) {
                    insideZones.add(Pair(zone, distance))
                }
            }
        }
        return insideZones
    }

    /**
     * Finds the nearest geofence zone from user location.
     */
    fun findNearestZone(userLat: Double, userLng: Double): Pair<GeofenceZone, Double>? {
        return activeMunicipalZones
            .map { Pair(it, calculateDistanceMeters(userLat, userLng, it.latitude, it.longitude)) }
            .minByOrNull { it.second }
    }
}
