package com.example.envii.pages.presentation

import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun ImagePreview(imageUri: Uri?, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember(imageUri) {
        imageUri?.let {
            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Button(onClick = {
            onDismiss()
        }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("Dismiss")
        }
    }
}
