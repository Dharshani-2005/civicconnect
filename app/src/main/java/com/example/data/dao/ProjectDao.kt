package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PublicProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: PublicProject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(projects: List<PublicProject>)

    @Update
    suspend fun update(project: PublicProject)

    @Query("SELECT * FROM public_projects ORDER BY id DESC")
    fun getAllFlow(): Flow<List<PublicProject>>

    @Query("SELECT * FROM public_projects WHERE wardCode = :wardCode ORDER BY id DESC")
    fun getByWardFlow(wardCode: String): Flow<List<PublicProject>>

    @Query("SELECT COUNT(*) FROM public_projects")
    suspend fun getCount(): Int
}
