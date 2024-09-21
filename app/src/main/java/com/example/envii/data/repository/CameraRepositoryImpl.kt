package com.example.envii.data.repository

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.example.envii.R
import com.example.envii.domain.repository.CameraRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    private val application: Application
): CameraRepository {
    override suspend fun takePhoto(controller: LifecycleCameraController) {
        controller.takePicture(
            ContextCompat.getMainExecutor(application),
            object : ImageCapture.OnImageCapturedCallback(){
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val matrix = Matrix().apply{
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }

                    val imageBitmap: Bitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,0,
                        image.width, image.height,
                        matrix,true
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        savePhoto(imageBitmap)
                    }

                    image.close()
                }
            }
        )
    }

    companion object {
        var imageMediaStoreUri: Uri? = null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun savePhoto(bitmap: Bitmap){
        withContext(Dispatchers.IO) {
            val resolver: ContentResolver = application.contentResolver

            val imageCollection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )

            val appName = application.getString(R.string.app_name)
            val timeInMillis = System.currentTimeMillis()

            val imageContentValues: ContentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "${timeInMillis}_image" + ".jpg"
                )
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + "/$appName"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.DATE_TAKEN, timeInMillis)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            imageMediaStoreUri = resolver.insert(
                imageCollection, imageContentValues
            )

            imageMediaStoreUri?.let { uri ->
                try {

                    resolver.openOutputStream(uri)?.let { outputStream ->
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG, 100, outputStream
                        )
                    }

                    imageContentValues.clear()
                    imageContentValues.put(
                        MediaStore.MediaColumns.IS_PENDING, 0
                    )
                    resolver.update(
                        uri, imageContentValues, null, null
                    )

                    cloudCon(uri)

                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                }
            }
        }
    }

    private fun cloudCon(imageUri: Uri){
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        if (userId == null) {
            Log.e("CloudUpload", "User is not authenticated")
            return
        }

        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val userFolderRef = storageRef.child("images/$userId/$fileName")

        try {
            val uploadTask = userFolderRef.putFile(imageUri)
            uploadTask.addOnSuccessListener {
                Log.d("CloudUpload", "Upload successful: $fileName")
            }.addOnFailureListener { exception ->
                Log.e("CloudUpload", "Upload failed: ${exception.message}")
            }.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                Log.d("CloudUpload", "Upload is $progress% done")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CloudUpload", "Exception during upload: ${e.message}")
        }
    }
}