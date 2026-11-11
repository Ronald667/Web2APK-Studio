package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_apps")
data class SavedAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val appName: String,
    val webUrl: String,
    val appDescription: String = "",
    val primaryColorHex: String = "#6750A4",
    val enableJavaScript: Boolean = true,
    val enableZoom: Boolean = true,
    val pullToRefresh: Boolean = true,
    val desktopMode: Boolean = false,
    val iconUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
