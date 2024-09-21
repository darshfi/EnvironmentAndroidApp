package com.example.envii.pages.presentation

import android.net.Uri
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import com.example.envii.data.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository
) : ViewModel() {

    fun takePhoto(controller: LifecycleCameraController, onPhotoTaken: (Uri?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            cameraRepository.takePhoto(controller, onPhotoTaken)
        }
    }
}