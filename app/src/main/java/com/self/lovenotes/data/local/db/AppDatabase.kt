package com.self.lovenotes.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.self.lovenotes.data.local.dao.PathDao
import com.self.lovenotes.data.local.entity.PathEntity

@Database(entities = [PathEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pathDao(): PathDao
}