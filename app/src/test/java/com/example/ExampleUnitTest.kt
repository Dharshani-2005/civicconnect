package com.example

import com.example.data.model.NotificationType
import com.example.service.AiComputerVisionService
import com.example.service.FleetTrackingService
import com.example.service.GeofenceService
import com.example.service.NotificationService
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun geofence_distanceCalculation_isAccurate() {
        // Distance between Anna Nagar (13.0850, 80.2150) and nearby point (13.0851, 80.2151)
        val distance = GeofenceService.calculateDistanceMeters(13.0850, 80.2150, 13.0851, 80.2151)
        assertTrue("Distance should be under 25 meters", distance < 25.0)
    }

    @Test
    fun geofence_detectionInsideZone_returnsZone() {
        // Point right inside Anna Nagar zone (Zone 01 radius 150m)
        val insideZones = GeofenceService.checkGeofences(13.0850, 80.2150)
        assertTrue("User should be inside at least 1 active zone", insideZones.isNotEmpty())
        assertEquals("ZONE-01", insideZones[0].first.id)
    }

    @Test
    fun notifications_postingAndReading_worksCorrectly() {
        val initialCount = NotificationService.notifications.value.size
        NotificationService.postNotification(
            title = "Test Push Notification",
            message = "Automated test alert message",
            type = NotificationType.MUNICIPAL_BROADCAST,
            severity = "NORMAL"
        )
        val afterCount = NotificationService.notifications.value.size
        assertEquals(initialCount + 1, afterCount)
        assertNotNull(NotificationService.currentPushBanner.value)
        assertEquals("Test Push Notification", NotificationService.currentPushBanner.value?.title)

        // Dismiss banner
        NotificationService.dismissPushBanner()
        assertNull(NotificationService.currentPushBanner.value)
    }

    @Test
    fun aiVision_benchmarkSamples_areProperlyStructured() {
        val samples = AiComputerVisionService.benchmarkSamples
        assertTrue("Should contain at least 5 standard civic defect benchmark samples", samples.size >= 5)

        val potholeSample = samples.find { it.category == "Roads" }
        assertNotNull(potholeSample)
        assertEquals("SAMPLE_POTHOLE_01", potholeSample?.id)
        assertTrue("Severity score must be critical (>80)", potholeSample!!.defaultResult.severityScore > 80)
        assertEquals(24, potholeSample.defaultResult.recommendedSlaHours)
    }

    @Test
    fun fleetTracking_initialVehicles_haveValidCoordinates() {
        val vehicles = FleetTrackingService.initialVehicles
        assertTrue("Fleet should have multiple active vehicles", vehicles.size >= 4)
        vehicles.forEach { v ->
            assertTrue("Lat should be in valid Chennai range (12.8 - 13.3)", v.currentLat in 12.8..13.3)
            assertTrue("Lng should be in valid Chennai range (80.1 - 80.4)", v.currentLng in 80.1..80.4)
            assertTrue("Speed should be non-negative", v.speedKmh >= 0)
        }
    }
}
