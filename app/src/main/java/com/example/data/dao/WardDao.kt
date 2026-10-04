package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Ward
import kotlinx.coroutines.flow.Flow

@Dao
interface WardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(wards: List<Ward>)

    @Query("SELECT * FROM wards ORDER BY name ASC")
    fun getAllWardsFlow(): Flow<List<Ward>>

    @Query("SELECT * FROM wards ORDER BY name ASC")
    suspend fun getAllWards(): List<Ward>

    @Query("SELECT * FROM wards WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): Ward?

    @Query("SELECT COUNT(*) FROM wards")
    suspend fun getCount(): Int
}
