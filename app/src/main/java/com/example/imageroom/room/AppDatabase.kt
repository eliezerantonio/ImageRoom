package com.example.imageroom.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.imageroom.model.ImageModel

@Database(entities = [ImageModel::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImagesDao
}