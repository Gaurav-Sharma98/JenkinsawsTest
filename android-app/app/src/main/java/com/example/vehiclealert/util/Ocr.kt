package com.example.vehiclealert.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.tasks.await
import java.io.File

object OcrUtil {
    suspend fun recognizeTextFromFile(context: Context, imageFile: File): String {
        val image = InputImage.fromFilePath(context, Uri.fromFile(imageFile))
        val recognizer = TextRecognition.getClient()
        val result = recognizer.process(image).await()
        return result.text
    }

    suspend fun recognizeTextFromUri(context: Context, uri: Uri): String {
        val image = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient()
        val result = recognizer.process(image).await()
        return result.text
    }
}
