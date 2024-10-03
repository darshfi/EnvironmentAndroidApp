package com.example.envii.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ImageProcessor {

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val imgSize = 224  // Model expects a 224x224 input
        val byteBuffer =
            ByteBuffer.allocateDirect(4 * imgSize * imgSize * 3)  // Allocate buffer for RGB values
        byteBuffer.order(ByteOrder.nativeOrder())  // Set byte order to native

        // Scale the bitmap to the required size
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imgSize, imgSize, true)
        val pixels = IntArray(imgSize * imgSize)
        scaledBitmap.getPixels(pixels, 0, imgSize, 0, 0, imgSize, imgSize)

        for (pixel in pixels) {
            // Extract RGB components and normalize to [0, 1]
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        return byteBuffer
    }
}
