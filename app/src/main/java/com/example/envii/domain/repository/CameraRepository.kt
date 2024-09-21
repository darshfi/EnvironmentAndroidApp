package com.example.envii.domain.repository

import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController

interface CameraRepository {

    suspend fun takePhoto(
        controller: LifecycleCameraController
    )
}