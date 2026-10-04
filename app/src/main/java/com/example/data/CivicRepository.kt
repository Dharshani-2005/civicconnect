package com.example.data

import com.example.data.model.AuditLog
import com.example.data.model.Escalation
import com.example.data.model.Grievance
import com.example.data.model.PublicProject
import com.example.data.model.RTIRequest
import com.example.data.model.User
import com.example.data.model.Ward
import com.example.service.DuplicateDetectionService
import com.example.service.EscalationService
import com.example.service.GeohashUtil
import com.example.service.HashUtil
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CivicRepository(private val db: CivicDatabase) {

    // Audit Log Operations
    val allAuditLogs: Flow<List<AuditLog>> = db.auditLogDao().getAllLogs()

    suspend fun logAuditEvent(actionType: String, userId: String, details: String) {
        val nowStr = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        db.auditLogDao().insert(
            AuditLog(
                actionType = actionType,
                userId = userId,
                details = details,
                timestamp = nowStr
            )
        )
    }

    // User Operations
    suspend fun registerUser(name: String, email: String, passwordRaw: String, role: String, wardCode: String, phone: String): Result<User> {
        val existing = db.userDao().getByEmail(email)
        if (existing != null) {
            return Result.failure(Exception("An account with email '$email' already exists."))
        }
        val user = User(
            name = name,
            email = email,
            passwordHash = HashUtil.sha256(passwordRaw),
            role = role,
            wardCode = wardCode,
            phone = phone
        )
        val id = db.userDao().insert(user)
        logAuditEvent("USER_REGISTER", email, "Registered role: $role in ward $wardCode")
        return Result.success(user.copy(id = id.toInt()))
    }

    suspend fun loginUser(email: String, passwordRaw: String): Result<User> {
        val user = db.userDao().getByEmail(email)
            ?: return Result.failure(Exception("No account found with email '$email'."))

        val hash = HashUtil.sha256(passwordRaw)
        if (user.passwordHash != hash) {
            logAuditEvent("LOGIN_FAILED", email, "Invalid password attempt")
            return Result.failure(Exception("Invalid password. Please check your credentials."))
        }
        logAuditEvent("USER_LOGIN", email, "Successful login as ${user.role}")
        return Result.success(user)
    }

    suspend fun loginByPhoneOrRole(phone: String, role: String): Result<User> {
        val cleanPhone = phone.trim().replace(" ", "").replace("-", "")
        var user = db.userDao().getByPhone(cleanPhone)
        if (user == null) {
            user = db.userDao().getFirstByRole(role)
        }
        if (user == null) {
            // Create a default user if none found
            val newUser = User(
                name = if (role == "CITIZEN") "Citizen ($cleanPhone)" else "$role Officer",
                email = "${cleanPhone.takeLast(6)}@civic.app",
                passwordHash = HashUtil.sha256("otp123456"),
                role = role,
                wardCode = "CHN-W-001",
                phone = "+91 $cleanPhone"
            )
            val newId = db.userDao().insert(newUser)
            user = newUser.copy(id = newId.toInt())
        }
        logAuditEvent("OTP_LOGIN", user.email, "Verified mobile OTP login as ${user.role}")
        return Result.success(user)
    }

    // Ward Operations
    val allWards: Flow<List<Ward>> = db.wardDao().getAllWardsFlow()
    suspend fun getWardByCode(code: String): Ward? = db.wardDao().getByCode(code)

    // Grievance Operations
    val allGrievances: Flow<List<Grievance>> = db.grievanceDao().getAllGrievancesFlow()

    fun getGrievancesByCitizen(citizenId: Int): Flow<List<Grievance>> =
        db.grievanceDao().getGrievancesByCitizen(citizenId)

    fun getGrievancesByWard(wardCode: String): Flow<List<Grievance>> =
        db.grievanceDao().getGrievancesByWard(wardCode)

    suspend fun getGrievanceById(id: Int): Grievance? = db.grievanceDao().getById(id)

    suspend fun checkDuplicate(
        title: String,
        description: String,
        category: String,
        lat: Double,
        lng: Double
    ): List<Pair<Grievance, Double>> {
        val geohash = GeohashUtil.encode(lat, lng, 6)
        return DuplicateDetectionService.findDuplicates(db.grievanceDao(), title, description, category, geohash)
    }

    suspend fun submitGrievance(
        title: String,
        description: String,
        category: String,
        lat: Double,
        lng: Double,
        anonymous: Boolean,
        citizenId: Int,
        wardCode: String,
        photoPath: String? = null
    ): Grievance {
        val nowMs = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val nowStr = sdf.format(Date(nowMs))

        val normalizedCategory = when {
            category.uppercase().contains("ROAD") -> "ROAD"
            category.uppercase().contains("WATER") -> "WATER"
            category.uppercase().contains("SANITATION") || category.uppercase().contains("GARBAGE") -> "SANITATION"
            category.uppercase().contains("ELECTRIC") || category.uppercase().contains("LIGHT") -> "ELECTRICITY"
            category.uppercase() in listOf("ROAD", "WATER", "SANITATION", "ELECTRICITY", "OTHER") -> category.uppercase()
            else -> "OTHER"
        }
        val normalizedWard = if (wardCode.isBlank() || wardCode == "W001") "CHN-W-001" else wardCode

        val slaDays = Grievance.getSLADays(normalizedCategory)
        val slaDeadlineStr = sdf.format(Date(nowMs + (slaDays * 86400000L)))

        val basePriority = Grievance.getBasePriority(normalizedCategory)
        val geohash = GeohashUtil.encode(lat, lng, 6)

        // Generate tracking ID: CC-YYYY-NNNNN
        val count = db.grievanceDao().getCount() + 1
        val trackingId = "CC-%d-%05d".format(2024, count)

        val grievance = Grievance(
            trackingId = trackingId,
            title = title,
            description = description,
            category = normalizedCategory,
            latitude = lat,
            longitude = lng,
            geohash = geohash,
            status = "SUBMITTED",
            priorityScore = basePriority,
            anonymous = anonymous,
            photoPath = photoPath,
            submittedAt = nowStr,
            slaDeadline = slaDeadlineStr,
            citizenId = citizenId,
            wardCode = normalizedWard
        )

        val id = db.grievanceDao().insert(grievance)
        logAuditEvent("PETITION_FILED", if (anonymous) "ANONYMOUS" else citizenId.toString(), "Filed petition $trackingId in ward $normalizedWard (Category: $normalizedCategory)")
        return grievance.copy(id = id.toInt())
    }

    suspend fun upvoteGrievance(id: Int) {
        db.grievanceDao().upvote(id)
    }

    suspend fun updateGrievanceStatus(id: Int, newStatus: String, officerNote: String?) {
        val existing = db.grievanceDao().getById(id) ?: return
        val nowStr = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        val resolvedTime = if (newStatus == "RESOLVED" || newStatus == "CLOSED") nowStr else existing.resolvedAt

        val updated = existing.copy(
            status = newStatus,
            officerNote = officerNote,
            resolvedAt = resolvedTime
        )
        db.grievanceDao().update(updated)
        logAuditEvent("STATUS_UPDATE", "OFFICER", "Updated complaint ${existing.trackingId} status to $newStatus")
    }

    // Escalation Operations
    suspend fun runSLACheck(): Int {
        return EscalationService.runSLACheck(db.grievanceDao(), db.escalationDao())
    }

    suspend fun getEscalationsForGrievance(grievanceId: Int): List<Escalation> {
        return db.escalationDao().getByGrievanceId(grievanceId)
    }

    // RTI Operations
    fun getRTIRequestsByCitizen(citizenId: Int): Flow<List<RTIRequest>> =
        db.rtiDao().getAllByCitizenFlow(citizenId)

    val allRTIRequests: Flow<List<RTIRequest>> = db.rtiDao().getAllFlow()

    suspend fun submitRTIRequest(
        citizenId: Int,
        citizenName: String,
        subject: String,
        department: String,
        description: String
    ): RTIRequest {
        val nowMs = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val nowStr = sdf.format(Date(nowMs))
        val deadlineStr = sdf.format(Date(nowMs + (30L * 86400000L)))

        val request = RTIRequest(
            citizenId = citizenId,
            citizenName = citizenName,
            subject = subject,
            department = department,
            description = description,
            submittedAt = nowStr,
            deadline = deadlineStr,
            status = "SUBMITTED"
        )
        val id = db.rtiDao().insert(request)
        return request.copy(id = id.toInt())
    }

    suspend fun updateRTIPdfPath(id: Int, pdfPath: String) {
        val rti = db.rtiDao().getById(id) ?: return
        db.rtiDao().update(rti.copy(pdfPath = pdfPath))
    }

    // Public Project Operations
    val allProjects: Flow<List<PublicProject>> = db.projectDao().getAllFlow()
    fun getProjectsByWard(wardCode: String): Flow<List<PublicProject>> = db.projectDao().getByWardFlow(wardCode)

    suspend fun addProject(project: PublicProject) {
        db.projectDao().insert(project)
    }
}
