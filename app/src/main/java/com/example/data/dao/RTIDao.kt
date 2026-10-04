package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RTIRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface RTIDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: RTIRequest): Long

    @Update
    suspend fun update(request: RTIRequest)

    @Query("SELECT * FROM rti_requests WHERE citizenId = :citizenId ORDER BY id DESC")
    fun getAllByCitizenFlow(citizenId: Int): Flow<List<RTIRequest>>

    @Query("SELECT * FROM rti_requests ORDER BY id DESC")
    fun getAllFlow(): Flow<List<RTIRequest>>

    @Query("SELECT * FROM rti_requests WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): RTIRequest?
}
