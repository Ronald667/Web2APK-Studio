package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedAppEntity::class], version = 3, exportSchema = false)
abstract class Web2ApkDatabase : RoomDatabase() {
    abstract fun savedAppDao(): SavedAppDao

    companion object {
        @Volatile
        private var INSTANCE: Web2ApkDatabase? = null

        fun getDatabase(context: Context): Web2ApkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Web2ApkDatabase::class.java,
                    "web2apk_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
