package com.example.imageroom.views


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.imageroom.R
import com.example.imageroom.model.ImageModel
import com.example.imageroom.viewModel.ImagesViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects



@Composable
fun AddPhotoView(viewModel: ImagesViewModel) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider", file
    )

    var image by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    val imageDefault = R.drawable.photo
    val permissionCheckResult =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        image = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it != null) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_LONG).show()
        }
    }
//Save en Gallery

    val saveImageInGallery = { imageUri: Uri ->

        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(context.createImageFileInGallery())

            inputStream?.use { input ->
                outputStream.use { output ->

                    input.copyTo(output)
                }
            }
            Toast.makeText(context, "Save on Gallery", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error when save image in Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    //save to room

    val saveImageRoom = { imageUri: Uri ->

        try {
            val imagePath = context.saveImageToRoom(imageUri)

            viewModel.insertImage(ImageModel(route = imagePath))

            Toast.makeText(context, "Save on Room,", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error when save image in Room", Toast.LENGTH_SHORT).show()
        }

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .clickable {
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
                .padding(16.dp, 8.dp),
            painter = rememberAsyncImagePainter(if (image.path?.isNotEmpty() == true) image else imageDefault),
            contentDescription = null
        )

        Button(onClick = {

//            saveImageInGallery(image)
            saveImageRoom(image)
        }) {
            Text(text = "Guardar foto")
        }


        Spacer(modifier = Modifier.height(25.dp))
    }
}

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}


fun Context.createImageFileInGallery(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"

    val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    return File(imageDir, "$imageFileName.jpg")

}



@SuppressLint("SimpleDateFormat")
fun Context.saveImageToRoom(imageUri: Uri): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_$timeStamp.jpg"

    val outputDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val outputFile = File(outputDir, imageFileName)

    val inputStream = contentResolver.openInputStream(imageUri)
    val outputStream = FileOutputStream(outputFile)

    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return outputFile.absolutePath

}


