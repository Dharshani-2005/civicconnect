package com.example.service

import com.example.data.CivicDatabase
import com.example.data.model.Grievance
import com.example.data.model.PublicProject
import com.example.data.model.User
import com.example.data.model.Ward
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataSeedService {

    suspend fun seedIfNeeded(db: CivicDatabase) {
        val wardCount = db.wardDao().getCount()
        if (wardCount > 0) return // Already seeded

        // 1. Seed 14 Wards across 6 cities
        val wards = listOf(
            Ward("CHN-W-001", "T Nagar", "Chennai", "K. R. Vairavan", "+91 98400 11223", 13.0418, 80.2341, 3.5),
            Ward("CHN-W-002", "Anna Nagar", "Chennai", "S. Premkumar", "+91 98400 22334", 13.0850, 80.2101, 4.0),
            Ward("CHN-W-003", "Adyar", "Chennai", "L. Meenakshi", "+91 98400 33445", 13.0012, 80.2565, 3.8),
            Ward("CHN-W-004", "Velachery", "Chennai", "R. Balaji", "+91 98400 44556", 12.9815, 80.2180, 5.0),
            Ward("CHN-W-005", "Tambaram", "Chennai", "M. Sundaram", "+91 98400 55667", 12.9249, 80.1000, 6.2),
            Ward("MUM-W-001", "Andheri East", "Mumbai", "A. K. Deshmukh", "+91 98200 12345", 19.1136, 72.8697, 4.5),
            Ward("MUM-W-002", "Bandra West", "Mumbai", "Meena Kulkarni", "+91 98200 23456", 19.0596, 72.8295, 3.2),
            Ward("MUM-W-003", "Dadar", "Mumbai", "V. R. Patil", "+91 98200 34567", 19.0178, 72.8478, 2.8),
            Ward("DEL-W-001", "Connaught Place", "Delhi", "Arjun Singh", "+91 98100 98765", 28.6315, 77.2167, 2.5),
            Ward("DEL-W-002", "Dwarka", "Delhi", "Pooja Sharma", "+91 98100 87654", 28.5921, 77.0460, 5.5),
            Ward("BLR-W-001", "Koramangala", "Bangalore", "S. Rao", "+91 98800 11122", 12.9352, 77.6245, 3.0),
            Ward("BLR-W-002", "Whitefield", "Bangalore", "N. Hegde", "+91 98800 22233", 12.9698, 77.7499, 6.0),
            Ward("HYD-W-001", "Hitech City", "Hyderabad", "T. Reddy", "+91 98490 33344", 17.4435, 78.3772, 4.2),
            Ward("KOL-W-001", "Salt Lake", "Kolkata", "B. Mukherjee", "+91 98300 44455", 22.5867, 88.4171, 4.8)
        )
        db.wardDao().insertAll(wards)

        // 2. Seed 3 Demo Users
        val users = listOf(
            User(
                id = 1,
                name = "System Administrator",
                email = "admin@civic.app",
                passwordHash = HashUtil.sha256("admin123"),
                role = "ADMIN",
                wardCode = "CHN-W-001",
                phone = "+91 90000 00001"
            ),
            User(
                id = 2,
                name = "Meena Kulkarni (Officer)",
                email = "officer@civic.app",
                passwordHash = HashUtil.sha256("officer123"),
                role = "OFFICER",
                wardCode = "CHN-W-001",
                phone = "+91 90000 00002"
            ),
            User(
                id = 3,
                name = "Ravi Kumar (Citizen)",
                email = "citizen@civic.app",
                passwordHash = HashUtil.sha256("citizen123"),
                role = "CITIZEN",
                wardCode = "CHN-W-001",
                phone = "+91 90000 00003"
            )
        )
        db.userDao().insertAll(users)

        // 3. Seed Sample Grievances
        val nowMs = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val nowStr = sdf.format(Date(nowMs))
        val pastDeadline = sdf.format(Date(nowMs - (2 * 86400000L))) // 2 days ago (SLA breached)
        val futureDeadline = sdf.format(Date(nowMs + (5 * 86400000L)))

        val grievances = listOf(
            Grievance(
                trackingId = "CC-2024-00001",
                title = "Deep pothole on Usman Road near bus stop 14",
                description = "Large dangerous pothole causing severe traffic jams and minor bike accidents during peak hours.",
                category = "ROAD",
                latitude = 13.0418,
                longitude = 80.2341,
                geohash = GeohashUtil.encode(13.0418, 80.2341, 6),
                status = "ESCALATED",
                priorityScore = 85,
                anonymous = false,
                submittedAt = nowStr,
                slaDeadline = pastDeadline, // Breached SLA
                upvoteCount = 12,
                citizenId = 3,
                wardCode = "CHN-W-001",
                officerNote = "Escalated to Department Head due to SLA breach."
            ),
            Grievance(
                trackingId = "CC-2024-00002",
                title = "Water main pipeline leakage near South Boag Road",
                description = "Clean drinking water leaking continuously for 48 hours onto the main road.",
                category = "WATER",
                latitude = 13.0430,
                longitude = 80.2355,
                geohash = GeohashUtil.encode(13.0430, 80.2355, 6),
                status = "IN_PROGRESS",
                priorityScore = 78,
                anonymous = false,
                submittedAt = nowStr,
                slaDeadline = futureDeadline,
                upvoteCount = 8,
                citizenId = 3,
                wardCode = "CHN-W-001",
                officerNote = "Technicians assigned. Pipeline repair in progress."
            ),
            Grievance(
                trackingId = "CC-2024-00003",
                title = "Garbage overflow at Anna Nagar 3rd Main Road junction",
                description = "Garbage bin not cleared for 4 days. Foul odor and health hazard for school children.",
                category = "SANITATION",
                latitude = 13.0850,
                longitude = 80.2101,
                geohash = GeohashUtil.encode(13.0850, 80.2101, 6),
                status = "SUBMITTED",
                priorityScore = 69,
                anonymous = true,
                submittedAt = nowStr,
                slaDeadline = futureDeadline,
                upvoteCount = 5,
                citizenId = 3,
                wardCode = "CHN-W-002"
            ),
            Grievance(
                trackingId = "CC-2024-00004",
                title = "Streetlights non-functional on Bandra Hill Road",
                description = "Entire stretch of 8 streetlights dark at night. Safety risk for pedestrians.",
                category = "ELECTRICITY",
                latitude = 19.0596,
                longitude = 72.8295,
                geohash = GeohashUtil.encode(19.0596, 72.8295, 6),
                status = "RESOLVED",
                priorityScore = 80,
                anonymous = false,
                submittedAt = nowStr,
                resolvedAt = nowStr,
                slaDeadline = futureDeadline,
                upvoteCount = 15,
                citizenId = 3,
                wardCode = "MUM-W-002",
                officerNote = "Fused bulbs and cable connection repaired."
            ),
            Grievance(
                trackingId = "CC-2024-00005",
                title = "Open storm water drain near Dwarka Sector 10 park",
                description = "Cover slab broken, leaving open drain 4 feet deep near playground.",
                category = "ROAD",
                latitude = 28.5921,
                longitude = 77.0460,
                geohash = GeohashUtil.encode(28.5921, 77.0460, 6),
                status = "SUBMITTED",
                priorityScore = 64,
                anonymous = false,
                submittedAt = nowStr,
                slaDeadline = futureDeadline,
                upvoteCount = 7,
                citizenId = 3,
                wardCode = "DEL-W-002"
            )
        )
        db.grievanceDao().insertAll(grievances)

        // 4. Seed Sample Public Projects
        val projects = listOf(
            PublicProject(
                name = "T Nagar Smart Drainage & Flood Prevention Project",
                department = "Public Works Department (PWD)",
                wardCode = "CHN-W-001",
                description = "Construction of high-capacity storm water drains and desilting canals to prevent monsoon waterlogging.",
                startDate = "2024-01-15",
                expectedEndDate = "2024-11-30",
                budgetAllocated = 45000000.0,
                budgetSpent = 28500000.0,
                status = "ONGOING"
            ),
            PublicProject(
                name = "Anna Nagar LED Streetlight Modernization",
                department = "Electrical Infrastructure",
                wardCode = "CHN-W-002",
                description = "Replacing legacy mercury vapor lights with energy-efficient smart LED fixtures.",
                startDate = "2024-02-01",
                expectedEndDate = "2024-06-30",
                budgetAllocated = 12000000.0,
                budgetSpent = 12000000.0,
                status = "COMPLETED"
            ),
            PublicProject(
                name = "Bandra West Promenade Rehabilitation",
                department = "Urban Beautification",
                wardCode = "MUM-W-002",
                description = "Repairing sea wall, footpaths, and installing public benches.",
                startDate = "2024-03-10",
                expectedEndDate = "2024-12-15",
                budgetAllocated = 32000000.0,
                budgetSpent = 9500000.0,
                status = "ONGOING"
            )
        )
        db.projectDao().insertAll(projects)
    }
}
