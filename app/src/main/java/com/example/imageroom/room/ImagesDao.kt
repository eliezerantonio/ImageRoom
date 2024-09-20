package com.example.imageroom.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.imageroom.model.ImageModel
import kotlinx.coroutines.flow.Flow


@Dao
interface ImagesDao {

    @Query("SELECT * FROM Images")
    fun getImages(): Flow<List<ImageModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(item: ImageModel)

    @Delete
    suspend fun deleteImage(item: ImageModel)




































}