package com.example.service

import com.example.data.dao.GrievanceDao
import com.example.data.model.Grievance
import kotlin.math.max
import kotlin.math.min

object DuplicateDetectionService {

    fun levenshtein(s: String, t: String): Int {
        val m = s.length
        val n = t.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (s[i - 1].lowercaseChar() == t[j - 1].lowercaseChar()) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[m][n]
    }

    fun similarity(a: String, b: String): Double {
        if (a.isEmpty() && b.isEmpty()) return 1.0
        if (a.isEmpty() || b.isEmpty()) return 0.0
        val maxLen = max(a.length, b.length)
        val dist = levenshtein(a, b)
        return 1.0 - (dist.toDouble() / maxLen.toDouble())
    }

    /**
     * Finds potential duplicates using Geohash prefix filter + Levenshtein distance check.
     */
    suspend fun findDuplicates(
        grievanceDao: GrievanceDao,
        title: String,
        description: String,
        category: String,
        geohash: String,
        threshold: Double = 0.72
    ): List<Pair<Grievance, Double>> {
        if (geohash.isEmpty()) return emptyList()

        // Phase 1: Geohash prefix filter (Precision 5 ~ 4.9km area)
        val prefix = if (geohash.length >= 5) geohash.substring(0, 5) else geohash
        val candidates = grievanceDao.getNearbyByCategory(prefix, category)

        val duplicates = mutableListOf<Pair<Grievance, Double>>()

        // Phase 2: Levenshtein distance similarity on title + description
        for (candidate in candidates) {
            val titleSim = similarity(title, candidate.title)
            val descSim = similarity(description, candidate.description)
            val combinedSim = (titleSim * 0.6) + (descSim * 0.4)

            if (combinedSim >= threshold) {
                duplicates.add(Pair(candidate, combinedSim))
            }
        }

        return duplicates.sortedByDescending { it.second }
    }
}
