package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedAppDao {
    @Query("SELECT * FROM saved_apps ORDER BY createdAt DESC")
    fun getAllApps(): Flow<List<SavedAppEntity>>

    @Query("SELECT * FROM saved_apps WHERE id = :appId")
    suspend fun getAppById(appId: Long): SavedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: SavedAppEntity): Long

    @Delete
    suspend fun deleteApp(app: SavedAppEntity)
}
