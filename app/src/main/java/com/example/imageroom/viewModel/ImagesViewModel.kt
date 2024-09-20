package com.example.imageroom.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.imageroom.model.ImageModel
import com.example.imageroom.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ImagesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application, AppDatabase::class.java,
        "images_database"
    ).build()

    private val _imagesList = MutableStateFlow<List<ImageModel>>(emptyList())

    val imageList = _imagesList.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            db.imageDao().getImages().collect { items ->
                _imagesList.value = items

            }

        }
    }

    fun insertImage(item: ImageModel) {
        viewModelScope.launch(Dispatchers.IO) {
            db.imageDao().insertImage(item)
        }
    }

    fun deleteImage(item: ImageModel) {
        viewModelScope.launch(Dispatchers.IO) {
            deletePhoto(item.route)
            db.imageDao().deleteImage(item)

        }
    }

    private fun deletePhoto(photoPhoto:String){
        val file = File(photoPhoto)
        if(file.exists()){
            file.delete()
        }
    }

}