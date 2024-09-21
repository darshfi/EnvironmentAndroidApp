package com.example.envii.ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class AiModel(context: Context) {
    private var interpreter: Interpreter? = null

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyImage(input: FloatArray): String {
        val output = Array(1) { FloatArray(2) }
        interpreter?.run(input, output)

        // Assuming the output has two probabilities for "good" and "no_good"
        return if (output[0][0] > output[0][1]) {
            "good"
        } else {
            "no_good"
        }
    }

    fun close() {
        interpreter?.close()
    }
}