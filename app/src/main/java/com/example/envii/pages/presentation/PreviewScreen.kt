package com.example.envii.pages.presentation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.envii.ai.AiModelHandler
import com.example.envii.ai.ImageProcessor
import com.example.envii.data.repository.CameraRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.random.Random

private lateinit var aiModelHandler: AiModelHandler

@Composable
fun PreviewScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val imageUrl = CameraRepositoryImpl.imageMediaStoreUri

    LaunchedEffect(Unit) {
        aiModelHandler = AiModelHandler(context) // Initialize AiModelHandler with context
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl ?: "https://picsum.photos/seed/${Random.nextInt()}/300/200"
            ),
            contentDescription = null,
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .fillMaxSize()
        )

        Row(
            modifier = Modifier
                .width(35.dp)
                .padding(top = 18.dp)
                .align(Alignment.TopStart)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        navController.navigate("home")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Decline",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        val imageFile = File(CameraRepositoryImpl.filePath)
                        if (imageFile.exists()) {
                            imageFile.delete()
                        }
                        navController.navigate("camera")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Decline",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(1.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        CoroutineScope(Dispatchers.IO).launch {
                            if (imageUrl != null) {
                                cloudCon(imageUrl)
                                handleImageUri(imageUrl, context)
                            }
                        }
                        navController.navigate("home")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Accept",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

private fun cloudCon(imageUri: Uri) {
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

private fun handleImageUri(uri: Uri, context: Context) {
    val bitmap: Bitmap? = ImageProcessor.getBitmapFromUri(context, uri)
    if (bitmap == null) {
        Log.e("Bitmap Error", "Failed to get bitmap from URI")
        return // Return early if bitmap is null
    }

    val result = aiModelHandler.runModel(bitmap)
    Log.d("AI Result", if (result == 1) "Good" else "No Good")
}