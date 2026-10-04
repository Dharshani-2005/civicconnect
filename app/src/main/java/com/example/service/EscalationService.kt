package com.example.service

import com.example.data.dao.EscalationDao
import com.example.data.dao.GrievanceDao
import com.example.data.model.Escalation
import com.example.data.model.Grievance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.PriorityQueue

object EscalationService {

    private fun getCurrentIsoTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun nextEscalationLevel(currentLevel: String): String? {
        return when (currentLevel.uppercase()) {
            "SUBMITTED" -> "OFFICER"
            "OFFICER" -> "DEPARTMENT_HEAD"
            "DEPARTMENT_HEAD" -> "COMMISSIONER"
            else -> null // Already at highest level
        }
    }

    suspend fun runSLACheck(
        grievanceDao: GrievanceDao,
        escalationDao: EscalationDao
    ): Int {
        val nowIso = getCurrentIsoTime()
        val breachedList = grievanceDao.getBreachedSLA(nowIso)
        if (breachedList.isEmpty()) return 0

        // Priority Queue processes most overdue SLA deadline first
        val pq = PriorityQueue<Grievance> { g1, g2 ->
            g1.slaDeadline.compareTo(g2.slaDeadline)
        }
        pq.addAll(breachedList)

        var escalatedCount = 0

        while (pq.isNotEmpty()) {
            val grievance = pq.poll() ?: break

            val currentEscalations = escalationDao.getByGrievanceId(grievance.id)
            val currentLevel = if (currentEscalations.isEmpty()) "SUBMITTED" else currentEscalations.first().escalatedTo

            val nextLevel = nextEscalationLevel(currentLevel)
            if (nextLevel != null) {
                // Log escalation record
                val escalation = Escalation(
                    grievanceId = grievance.id,
                    escalatedTo = nextLevel,
                    reason = "SLA_BREACH",
                    escalatedAt = nowIso
                )
                escalationDao.insert(escalation)

                // Update grievance status
                val updatedGrievance = grievance.copy(
                    status = "ESCALATED",
                    priorityScore = (grievance.priorityScore + 15).coerceAtMost(100)
                )
                grievanceDao.update(updatedGrievance)
                escalatedCount++
            }
        }

        return escalatedCount
    }
}
