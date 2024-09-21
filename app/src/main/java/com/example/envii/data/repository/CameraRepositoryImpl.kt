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
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.example.envii.R
import com.example.envii.ai.AiModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    private val application: Application
): CameraRepository {
    override suspend fun takePhoto(controller: LifecycleCameraController, onPhotoTaken: (Uri?) -> Unit) {
        controller.takePicture(
            ContextCompat.getMainExecutor(application),
            object : ImageCapture.OnImageCapturedCallback(){
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    Log.d("AiModel", "Capture done")

                    val matrix = Matrix().apply{
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }

                    val imageBitmap: Bitmap = image.toBitmap()
                    Log.d("AiModel", "Going for savePhoto")

                    val rotatedBitmap = Bitmap.createBitmap(
                        imageBitmap,
                        0, 0,
                        image.width, image.height,
                        matrix,
                        true
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        savePhoto(rotatedBitmap) { uri ->
                            onPhotoTaken(uri)
                        }
                    }

                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("CameraRepository", "Photo capture failed: ${exception.message}")
                }
            }
        )
    }

    companion object {
        var imageMediaStoreUri: Uri? = null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun savePhoto(bitmap: Bitmap, onUriReady: (Uri?) -> Unit){
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

                    Log.d("AiModel", "Image Saved: $uri")

                    cloudCon(uri)

                    /*
                    // Use the AI model for classification
                    val aiModel = AiModel(application)
                    val inputArray = preprocessBitmap(bitmap) // Function to preprocess the image into a FloatArray
                    val result = aiModel.classifyImage(inputArray)

                    // Log the result
                    Log.d("AiModel", "Classification Result: $result")

                    // Optionally handle the result (e.g., show a toast, update UI)
                    aiModel.close() // Close the model when done
                     */
                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                }
            }

            onUriReady(imageMediaStoreUri)
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

    private fun preprocessBitmap(bitmap: Bitmap): FloatArray {
        // Resize the bitmap to the dimensions expected by your model
        val width = 224
        val height = 224
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

        // Create a FloatArray to hold the normalized pixel values
        val floatArray = FloatArray(width * height * 3)

        // Normalize the pixel values
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = resizedBitmap.getPixel(x, y)
                // Get the RGB values
                val r = ((pixel shr 16) and 0xFF) / 255.0f // Red
                val g = ((pixel shr 8) and 0xFF) / 255.0f  // Green
                val b = (pixel and 0xFF) / 255.0f           // Blue

                // Store the normalized values in the FloatArray
                floatArray[(y * width + x) * 3 + 0] = r
                floatArray[(y * width + x) * 3 + 1] = g
                floatArray[(y * width + x) * 3 + 2] = b
            }
        }

        // Return the processed float array
        return floatArray
    }
}