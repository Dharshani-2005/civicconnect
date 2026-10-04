package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FleetVehicle
import com.example.data.model.GeofenceZone
import com.example.data.model.Grievance
import com.example.data.model.NotificationType
import com.example.service.GeofenceService
import com.example.service.NotificationService
import com.example.ui.CivicViewModel
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MapHotspotScreen(
    viewModel: CivicViewModel,
    grievances: List<Grievance>,
    onGrievanceClick: (Grievance) -> Unit
) {
    // GIS Layer States
    var showGrievancesLayer by remember { mutableStateOf(true) }
    var showGeofenceLayer by remember { mutableStateOf(true) }
    var showFleetLayer by remember { mutableStateOf(true) }
    var showHeatmapLayer by remember { mutableStateOf(false) }

    val geofenceZones = remember { GeofenceService.activeMunicipalZones }
    val liveFleet by viewModel.liveFleet.collectAsState()

    var selectedGrievance by remember { mutableStateOf<Grievance?>(null) }
    var selectedGeofence by remember { mutableStateOf<GeofenceZone?>(null) }
    var selectedVehicle by remember { mutableStateOf<FleetVehicle?>(null) }

    // Simulated User GPS Location for Geo-fence proximity testing
    var userSimLat by remember { mutableStateOf(13.0827) }
    var userSimLng by remember { mutableStateOf(80.2707) }
    var geofenceAlertActive by remember { mutableStateOf(false) }

    val centroids = viewModel.getHotspotCentroids(grievances)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GIS Map & Fleet Tracking",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIVE GIS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessEmerald
                            )
                        }
                    }
                    Text(
                        text = "Spatial Layers: Grievances, Geo-fences & Municipal Fleet",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quick Geo-Fence Alert Simulator
                Button(
                    onClick = {
                        // Simulate entering Anna Nagar Zone
                        userSimLat = 13.0850
                        userSimLng = 80.2150
                        geofenceAlertActive = true
                        NotificationService.postNotification(
                            title = "⚠️ Entered Hazard Geo-Fence: Ward 01",
                            message = "Active stormwater drain excavation on 3rd Avenue. Diversion route recommended.",
                            type = NotificationType.GEOFENCE_ALERT,
                            severity = "HIGH",
                            actionRoute = "map"
                        )
                    },
                    modifier = Modifier.testTag("simulate_geofence_entry_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
                ) {
                    Icon(Icons.Default.NearMe, contentDescription = "Test Geofence", modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Fence", fontSize = 11.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // GIS Layer Filter Toggles
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = showGrievancesLayer,
                    onClick = { showGrievancesLayer = !showGrievancesLayer },
                    label = { Text("📌 Grievances (${grievances.size})", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = showGeofenceLayer,
                    onClick = { showGeofenceLayer = !showGeofenceLayer },
                    label = { Text("📡 Geo-Fences (${geofenceZones.size})", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentAmber,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = showFleetLayer,
                    onClick = { showFleetLayer = !showFleetLayer },
                    label = { Text("🚚 Live Fleet (${liveFleet.size})", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SuccessEmerald,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = showHeatmapLayer,
                    onClick = { showHeatmapLayer = !showHeatmapLayer },
                    label = { Text("🔥 Heatmap (k=5)", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ErrorRose,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive GIS Map Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // 1. Draw GIS Base Grid & Municipal Road Arterials
                        val roadPaint = Color(0xFF1E293B)
                        val arterialPaint = Color(0xFF334155)

                        // Main Arterial Corridors
                        drawLine(arterialPaint, Offset(0f, canvasHeight * 0.45f), Offset(canvasWidth, canvasHeight * 0.45f), strokeWidth = 14f)
                        drawLine(arterialPaint, Offset(0f, canvasHeight * 0.7f), Offset(canvasWidth, canvasHeight * 0.7f), strokeWidth = 10f)
                        drawLine(arterialPaint, Offset(canvasWidth * 0.35f, 0f), Offset(canvasWidth * 0.35f, canvasHeight), strokeWidth = 12f)
                        drawLine(arterialPaint, Offset(canvasWidth * 0.68f, 0f), Offset(canvasWidth * 0.68f, canvasHeight), strokeWidth = 10f)

                        // Secondary Grid
                        for (x in 0..canvasWidth.toInt() step 70) {
                            drawLine(roadPaint, Offset(x.toFloat(), 0f), Offset(x.toFloat(), canvasHeight), strokeWidth = 2f)
                        }
                        for (y in 0..canvasHeight.toInt() step 70) {
                            drawLine(roadPaint, Offset(0f, y.toFloat()), Offset(canvasWidth, y.toFloat()), strokeWidth = 2f)
                        }

                        // 2. Draw Municipal Ward Boundaries (Polygon contours)
                        val wardBorderPaint = Color(0xFF475569)
                        drawRect(color = wardBorderPaint, topLeft = Offset(canvasWidth * 0.05f, canvasHeight * 0.08f), size = androidx.compose.ui.geometry.Size(canvasWidth * 0.4f, canvasHeight * 0.38f), style = Stroke(width = 2f))
                        drawRect(color = wardBorderPaint, topLeft = Offset(canvasWidth * 0.5f, canvasHeight * 0.08f), size = androidx.compose.ui.geometry.Size(canvasWidth * 0.45f, canvasHeight * 0.38f), style = Stroke(width = 2f))
                        drawRect(color = wardBorderPaint, topLeft = Offset(canvasWidth * 0.05f, canvasHeight * 0.52f), size = androidx.compose.ui.geometry.Size(canvasWidth * 0.4f, canvasHeight * 0.42f), style = Stroke(width = 2f))
                        drawRect(color = wardBorderPaint, topLeft = Offset(canvasWidth * 0.5f, canvasHeight * 0.52f), size = androidx.compose.ui.geometry.Size(canvasWidth * 0.45f, canvasHeight * 0.42f), style = Stroke(width = 2f))

                        // 3. Draw Spatial Heatmap Clusters if active
                        if (showHeatmapLayer) {
                            centroids.forEach { centroid ->
                                val cx = (canvasWidth * 0.2f) + ((centroid.first.hashCode() % 10) / 10f) * (canvasWidth * 0.6f)
                                val cy = (canvasHeight * 0.2f) + ((centroid.second.hashCode() % 10) / 10f) * (canvasHeight * 0.6f)

                                drawCircle(
                                    color = ErrorRose.copy(alpha = 0.25f),
                                    radius = 110f,
                                    center = Offset(cx, cy)
                                )
                                drawCircle(
                                    color = AccentAmber.copy(alpha = 0.4f),
                                    radius = 65f,
                                    center = Offset(cx, cy)
                                )
                            }
                        }

                        // 4. Draw Geo-Fence Circular Buffer Zones
                        if (showGeofenceLayer) {
                            geofenceZones.forEachIndexed { idx, zone ->
                                val fx = (canvasWidth * 0.15f) + ((zone.latitude.hashCode() % 8) / 8f) * (canvasWidth * 0.7f)
                                val fy = (canvasHeight * 0.15f) + ((zone.longitude.hashCode() % 8) / 8f) * (canvasHeight * 0.7f)
                                val color = if (zone.severity == "CRITICAL") ErrorRose else AccentAmber

                                // Buffer circle
                                drawCircle(
                                    color = color.copy(alpha = 0.18f),
                                    radius = (zone.radiusMeters * 0.55).toFloat(),
                                    center = Offset(fx, fy)
                                )
                                drawCircle(
                                    color = color,
                                    radius = (zone.radiusMeters * 0.55).toFloat(),
                                    center = Offset(fx, fy),
                                    style = Stroke(width = 3f)
                                )
                            }
                        }

                        // 5. Draw Grievance Pins
                        if (showGrievancesLayer) {
                            grievances.take(25).forEachIndexed { idx, g ->
                                val px = (canvasWidth * 0.1f) + ((g.latitude.hashCode() % 100) / 100f) * (canvasWidth * 0.8f)
                                val py = (canvasHeight * 0.1f) + ((g.longitude.hashCode() % 100) / 100f) * (canvasHeight * 0.8f)

                                val pinColor = when (g.status) {
                                    "SUBMITTED" -> AccentAmber
                                    "IN_PROGRESS" -> PrimaryBlue
                                    "RESOLVED" -> SuccessEmerald
                                    "CLOSED" -> Color.Gray
                                    else -> ErrorRose
                                }

                                drawCircle(color = pinColor, radius = 12f, center = Offset(px, py))
                                drawCircle(color = Color.White, radius = 4f, center = Offset(px, py))
                            }
                        }

                        // 6. Draw Live Moving Municipal Fleet Vehicles
                        if (showFleetLayer) {
                            liveFleet.forEach { vehicle ->
                                val vx = (canvasWidth * 0.12f) + ((vehicle.currentLat.hashCode() % 100) / 100f) * (canvasWidth * 0.76f)
                                val vy = (canvasHeight * 0.12f) + ((vehicle.currentLng.hashCode() % 100) / 100f) * (canvasHeight * 0.76f)

                                // Vehicle pulse ring
                                drawCircle(color = SuccessEmerald.copy(alpha = 0.35f), radius = 22f, center = Offset(vx, vy))
                                drawCircle(color = SuccessEmerald, radius = 14f, center = Offset(vx, vy))
                                drawCircle(color = Color.White, radius = 6f, center = Offset(vx, vy))
                            }
                        }
                    }

                    // Floating GIS Telemetry Chips & Overlays
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🛰️ GIS: 5 Wards Monitored",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📡 Geo-Fences: ${geofenceZones.size} Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🚚 Active Fleet: ${liveFleet.size} Trucks",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessEmerald
                            )
                        }
                    }

                    // Bottom Floating Quick Selectors for Pins / Vehicles / Fences
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedVehicle = liveFleet.firstOrNull()
                                selectedGeofence = null
                                selectedGrievance = null
                            },
                            modifier = Modifier.weight(1f).testTag("gis_inspect_fleet_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("🚚 Inspect Fleet", fontSize = 10.sp, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                selectedGeofence = geofenceZones.firstOrNull()
                                selectedVehicle = null
                                selectedGrievance = null
                            },
                            modifier = Modifier.weight(1f).testTag("gis_inspect_fence_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("📡 Inspect Fence", fontSize = 10.sp, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                selectedGrievance = grievances.firstOrNull()
                                selectedVehicle = null
                                selectedGeofence = null
                            },
                            modifier = Modifier.weight(1f).testTag("gis_inspect_pin_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("📌 Inspect Pin", fontSize = 10.sp, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inspector Detail Card
            if (selectedVehicle != null) {
                val v = selectedVehicle!!
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("vehicle_detail_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SuccessEmerald.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DirectionsCar, contentDescription = "Vehicle", tint = SuccessEmerald, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = v.vehicleName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${v.vehicleNumber} • Driver: ${v.driverName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            IconButton(onClick = { selectedVehicle = null }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Speed: ${v.speedKmh} km/h", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Status: ${v.status.replace("_", " ")}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessEmerald)
                            Text(text = "ETA: ${v.etaMinutes} mins", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Assigned Task: ${v.assignedGrievanceTitle ?: "Routine Ward Patrol"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (selectedGeofence != null) {
                val gf = selectedGeofence!!
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("geofence_detail_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentAmber.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Geofence", tint = AccentAmber, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = gf.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                            }
                            IconButton(onClick = { selectedGeofence = null }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = gf.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Diversion: ${gf.diversionRoute ?: "Standard Traffic Flow"} • Radius: ${gf.radiusMeters.toInt()}m", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (selectedGrievance != null) {
                val g = selectedGrievance!!
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("grievance_pin_detail_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${g.trackingId} • ${g.category}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { selectedGrievance = null }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(text = g.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Status: ${g.status}", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { onGrievanceClick(g) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("View Details", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
