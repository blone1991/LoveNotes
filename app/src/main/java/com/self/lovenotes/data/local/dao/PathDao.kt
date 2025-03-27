package com.self.lovenotes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.self.lovenotes.data.local.entity.PathEntity

@Dao
interface PathDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(path: PathEntity)

    @Query("SELECT * FROM gps_memory ORDER BY timestamp ASC")
    suspend fun getPaths(): List<PathEntity>

    @Query("DELETE FROM gps_memory")
    suspend fun deletePaths()
}