package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Grievance
import kotlinx.coroutines.flow.Flow

@Dao
interface GrievanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(grievance: Grievance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(grievances: List<Grievance>)

    @Update
    suspend fun update(grievance: Grievance)

    @Query("SELECT * FROM grievances ORDER BY id DESC")
    fun getAllGrievancesFlow(): Flow<List<Grievance>>

    @Query("SELECT * FROM grievances ORDER BY id DESC")
    suspend fun getAllGrievances(): List<Grievance>

    @Query("SELECT * FROM grievances WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Grievance?

    @Query("SELECT * FROM grievances WHERE trackingId = :trackingId LIMIT 1")
    suspend fun getByTrackingId(trackingId: String): Grievance?

    @Query("SELECT * FROM grievances WHERE geohash LIKE :prefix || '%' AND category = :category AND status != 'RESOLVED' AND status != 'CLOSED'")
    suspend fun getNearbyByCategory(prefix: String, category: String): List<Grievance>

    @Query("SELECT * FROM grievances WHERE slaDeadline < :nowIso AND status != 'RESOLVED' AND status != 'CLOSED'")
    suspend fun getBreachedSLA(nowIso: String): List<Grievance>

    @Query("SELECT * FROM grievances WHERE citizenId = :citizenId ORDER BY id DESC")
    fun getGrievancesByCitizen(citizenId: Int): Flow<List<Grievance>>

    @Query("SELECT * FROM grievances WHERE wardCode = :wardCode ORDER BY priorityScore DESC")
    fun getGrievancesByWard(wardCode: String): Flow<List<Grievance>>

    @Query("UPDATE grievances SET upvoteCount = upvoteCount + 1, priorityScore = MIN(priorityScore + 2, 100) WHERE id = :id")
    suspend fun upvote(id: Int)

    @Query("SELECT COUNT(*) FROM grievances")
    suspend fun getCount(): Int
}
