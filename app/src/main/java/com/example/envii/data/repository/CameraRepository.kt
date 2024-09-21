package com.example.envii.data.repository

import android.net.Uri
import androidx.camera.view.LifecycleCameraController

interface CameraRepository {
    suspend fun takePhoto(controller: LifecycleCameraController, onPhotoTaken: (Uri?) -> Unit)
}