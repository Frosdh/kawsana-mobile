package com.example.app_practicas_m5a.presentacion.ui

import android.content.Context
import android.graphics.Bitmap
import com.example.app_practicas_m5a.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Clasificacion_IA(private val context: Context) {

    // Lista de categorías (asegúrate que coincida con el modelo TFLite)
    private val classes = arrayOf(
        "Orgánico",
        "Metal",
        "Tapas-Metal",
        "Vidrio",
        "Plástico-Duro",
        "Plástico-Suave",
        "Papel",
        "Cartón",
        "Plástico",
        "Otros"
    )

    fun runModel(bitmap: Bitmap, inputSize: Int): String {
        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val byteBuffer = convertBitmapToByteBuffer(resized, inputSize)

        val model = ModelUnquant.newInstance(context)
        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, inputSize, inputSize, 3), DataType.FLOAT32)
        inputFeature.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature)
        val confidences = outputs.outputFeature0AsTensorBuffer.floatArray
        model.close()

        // Ordenar resultados por mayor confianza
        val resultList = confidences.mapIndexed { index, confidence ->
            "${classes[index]}: ${(confidence * 100).toInt()}%"
        }.sortedByDescending {
            it.substringAfter(": ").substringBefore("%").toInt()
        }

        return resultList.joinToString("\n")
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap, inputSize: Int): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixelIndex = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixel = intValues[pixelIndex++]
                byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
                byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
                byteBuffer.putFloat((pixel and 0xFF) / 255f)
            }
        }
        return byteBuffer
    }
}
