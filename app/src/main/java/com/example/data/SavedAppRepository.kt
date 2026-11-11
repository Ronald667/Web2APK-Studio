package com.example.data

import kotlinx.coroutines.flow.Flow

class SavedAppRepository(private val dao: SavedAppDao) {
    val allApps: Flow<List<SavedAppEntity>> = dao.getAllApps()

    suspend fun insert(app: SavedAppEntity): Long = dao.insertApp(app)

    suspend fun delete(app: SavedAppEntity) = dao.deleteApp(app)

    suspend fun getById(id: Long): SavedAppEntity? = dao.getAppById(id)
}
