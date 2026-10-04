package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Escalation
import kotlinx.coroutines.flow.Flow

@Dao
interface EscalationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(escalation: Escalation): Long

    @Query("SELECT * FROM escalations WHERE grievanceId = :grievanceId ORDER BY id DESC")
    suspend fun getByGrievanceId(grievanceId: Int): List<Escalation>

    @Query("SELECT * FROM escalations WHERE grievanceId = :grievanceId ORDER BY id DESC")
    fun getByGrievanceIdFlow(grievanceId: Int): Flow<List<Escalation>>

    @Query("SELECT * FROM escalations ORDER BY id DESC")
    fun getAllFlow(): Flow<List<Escalation>>
}
