package com.example.service

import com.example.data.model.FleetVehicle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

object FleetTrackingService {

    val initialVehicles: List<FleetVehicle> = listOf(
        FleetVehicle(
            id = "VEH-01",
            vehicleNumber = "TN-01-GA-4892",
            type = "ROAD_PATCH_VAN",
            vehicleName = "Cold Mix Bitumen Asphalt Van #12",
            driverName = "M. Selvam",
            driverPhone = "+91 98401 23890",
            currentLat = 13.0827,
            currentLng = 80.2707,
            headingDegrees = 45f,
            speedKmh = 32,
            status = "EN_ROUTE_TO_SITE",
            assignedGrievanceId = "GRV-ROAD-402",
            assignedGrievanceTitle = "Deep Pothole at 4th Main Road",
            destinationWard = "Ward 01 (Anna Nagar)",
            etaMinutes = 14
        ),
        FleetVehicle(
            id = "VEH-02",
            vehicleNumber = "TN-02-CM-8104",
            type = "SUCTION_JETTING",
            vehicleName = "Super Sucker Jetting Tanker #04",
            driverName = "K. Anbazhagan",
            driverPhone = "+91 97910 88231",
            currentLat = 13.0450,
            currentLng = 80.2310,
            headingDegrees = 180f,
            speedKmh = 18,
            status = "ON_SITE_REPAIRING",
            assignedGrievanceId = "GRV-WATER-219",
            assignedGrievanceTitle = "Drainage Overflow Near Market",
            destinationWard = "Ward 02 (T. Nagar)",
            etaMinutes = 0
        ),
        FleetVehicle(
            id = "VEH-03",
            vehicleNumber = "TN-04-EL-1920",
            type = "ELECTRICAL_CRANE",
            vehicleName = "Hydraulic Sky-Lift Crane #07",
            driverName = "R. Murugan",
            driverPhone = "+91 94440 91024",
            currentLat = 13.0110,
            currentLng = 80.2080,
            headingDegrees = 90f,
            speedKmh = 28,
            status = "EN_ROUTE_TO_SITE",
            assignedGrievanceId = "GRV-ELEC-108",
            assignedGrievanceTitle = "Fallen HT Pole Replacement",
            destinationWard = "Ward 04 (Guindy)",
            etaMinutes = 22
        ),
        FleetVehicle(
            id = "VEH-04",
            vehicleNumber = "TN-07-SW-6321",
            type = "GARBAGE_COMPACTOR",
            vehicleName = "Hydraulic Waste Compactor #18",
            driverName = "P. Kathiresan",
            driverPhone = "+91 98840 55192",
            currentLat = 12.9800,
            currentLng = 80.2240,
            headingDegrees = 270f,
            speedKmh = 24,
            status = "EN_ROUTE_TO_SITE",
            assignedGrievanceId = "GRV-SANI-305",
            assignedGrievanceTitle = "Bulk Solid Waste Clearance",
            destinationWard = "Ward 05 (Velachery)",
            etaMinutes = 9
        )
    )

    /**
     * Emits continuous vehicle telemetry stream with realistic GPS coordinate drift and speed changes.
     */
    fun getLiveFleetStream(): Flow<List<FleetVehicle>> = flow {
        var currentList = initialVehicles
        while (true) {
            emit(currentList)
            delay(3500)
            currentList = currentList.map { vehicle ->
                val latDrift = (Random.nextDouble(-0.0004, 0.0004))
                val lngDrift = (Random.nextDouble(-0.0004, 0.0004))
                val newSpeed = if (vehicle.status == "ON_SITE_REPAIRING") 0 else Random.nextInt(20, 45)
                val newEta = if (vehicle.etaMinutes > 1) vehicle.etaMinutes - 1 else if (vehicle.status == "ON_SITE_REPAIRING") 0 else 15

                vehicle.copy(
                    currentLat = vehicle.currentLat + latDrift,
                    currentLng = vehicle.currentLng + lngDrift,
                    speedKmh = newSpeed,
                    etaMinutes = newEta
                )
            }
        }
    }
}
