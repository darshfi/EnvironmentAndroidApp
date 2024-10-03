package com.example.envii.ai

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer

class AiModelHandler(context: Context) {

    private val tfliteInterpreter: Interpreter

    init {
        val modelFile = FileUtil.loadMappedFile(context, "model.tflite")
        tfliteInterpreter = Interpreter(modelFile)
    }

    fun runModel(bitmap: Bitmap): Int {
        // Use the existing ImageProcessor to preprocess the image
        val inputBuffer = ImageProcessor.preprocessImage(bitmap)

        // Prepare the output buffer (shape [1, 2] for the two classes)
        val outputBuffer = Array(1) { FloatArray(2) }

        // Run the model
        tfliteInterpreter.run(inputBuffer, outputBuffer)

        // Analyze the output to determine the result
        return if (outputBuffer[0][0] > outputBuffer[0][1]) 1 else 0 // "good" is 1, "no_good" is 0
    }
}
