package com.example.envii.pages.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.envii.MainActivity
import androidx.lifecycle.compose.LocalLifecycleOwner as LocalLifecycleOwner1

@Composable
fun CameraScreen(
    modifier: Modifier,
    navController: NavController,
    cameraViewModel: CameraViewModel,
    activity: Activity
) {
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showPreview by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val lifecycleOwner = LocalLifecycleOwner1.current

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply{
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        Intent(
                            Intent.ACTION_PICK,
                            Uri.parse(
                                "content://media/internal/images/media"
                            )
                        ).also {
                            activity.startActivity(it)
                        }
                    },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Open Gallery",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(1.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if((activity as MainActivity).arePermissionsGranted()){
                            Log.d("AiModel", "CLicked")
                            cameraViewModel.takePhoto(controller) { uri ->
                                imageUri = uri
                                showPreview = true
                            }
                            Log.d("AiModel", "onTakePhoto should be done")
                        }
                    },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.CameraEnhance,
                    contentDescription = "Take Photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        controller.cameraSelector =
                            if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else{
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                    },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
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
                        navController.navigate("home")
                    },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Go Home",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        if (showPreview && imageUri != null) {
            ImagePreview(imageUri) {
                showPreview = false
            }
        }

    }
}