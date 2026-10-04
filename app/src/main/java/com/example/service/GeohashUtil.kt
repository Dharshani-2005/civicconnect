package com.example.service

import kotlin.math.abs
import kotlin.random.Random

object GeohashUtil {
    private const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"

    fun encode(lat: Double, lng: Double, precision: Int = 6): String {
        var isEven = true
        var latMin = -90.0
        var latMax = 90.0
        var lngMin = -180.0
        var lngMax = 180.0

        var bit = 0
        var ch = 0
        val geohash = StringBuilder()

        while (geohash.length < precision) {
            if (isEven) {
                val mid = (lngMin + lngMax) / 2
                if (lng >= mid) {
                    ch = ch or (1 shl (4 - bit))
                    lngMin = mid
                } else {
                    lngMax = mid
                }
            } else {
                val mid = (latMin + latMax) / 2
                if (lat >= mid) {
                    ch = ch or (1 shl (4 - bit))
                    latMin = mid
                } else {
                    latMax = mid
                }
            }
            isEven = !isEven

            if (bit < 4) {
                bit++
            } else {
                geohash.append(BASE32[ch])
                bit = 0
                ch = 0
            }
        }
        return geohash.toString()
    }

    /**
     * K-Means clustering algorithm for geographic hotspot detection.
     * Takes array of Pair(lat, lng), returns cluster centroids.
     */
    fun kMeans(points: List<Pair<Double, Double>>, k: Int = 5, maxIter: Int = 50): List<Pair<Double, Double>> {
        if (points.isEmpty()) return emptyList()
        val actualK = k.coerceAtMost(points.size)

        // Fixed seed for deterministic visual hotspots
        val random = Random(42)
        var centroids = points.shuffled(random).take(actualK).toMutableList()

        for (iter in 0 until maxIter) {
            val clusters = Array(actualK) { mutableListOf<Pair<Double, Double>>() }

            // Assignment step
            for (p in points) {
                var nearestIdx = 0
                var minDist = Double.MAX_VALUE
                for (i in 0 until actualK) {
                    val dist = distanceSquared(p, centroids[i])
                    if (dist < minDist) {
                        minDist = dist
                        nearestIdx = i
                    }
                }
                clusters[nearestIdx].add(p)
            }

            // Update step
            var converged = true
            val newCentroids = mutableListOf<Pair<Double, Double>>()
            for (i in 0 until actualK) {
                if (clusters[i].isEmpty()) {
                    newCentroids.add(centroids[i])
                    continue
                }
                val meanLat = clusters[i].map { it.first }.average()
                val meanLng = clusters[i].map { it.second }.average()
                val newC = Pair(meanLat, meanLng)
                if (distanceSquared(newC, centroids[i]) > 0.000001) {
                    converged = false
                }
                newCentroids.add(newC)
            }
            centroids = newCentroids
            if (converged) break
        }
        return centroids
    }

    private fun distanceSquared(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Double {
        val dLat = p1.first - p2.first
        val dLng = p1.second - p2.second
        return dLat * dLat + dLng * dLng
    }
}
