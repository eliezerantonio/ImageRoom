package com.example.imageroom.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Images")
data class ImageModel(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val route: String

)
