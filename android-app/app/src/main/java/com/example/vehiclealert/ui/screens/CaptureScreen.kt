package com.example.vehiclealert.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.vehiclealert.data.AppDatabase
import com.example.vehiclealert.util.LocationUtil
import com.example.vehiclealert.util.NotificationsUtil
import com.example.vehiclealert.util.OcrUtil
import com.example.vehiclealert.util.PlateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CaptureScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val capture = remember { ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_AUTO).build() }
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Capture vehicle plate and notify owner")
        androidx.compose.ui.viewinterop.AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)
            bindCamera(ctx, lifecycleOwner, previewView, capture)
            previewView
        }, modifier = Modifier.weight(1f))

        Button(onClick = { takeAndProcessPhoto(context, capture) }) {
            Text("Capture & Process")
        }
        Button(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) { Text("Back") }
    }
}

private fun bindCamera(context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView, imageCapture: ImageCapture) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (_: Exception) { }
    }, ContextCompat.getMainExecutor(context))
}

private fun takeAndProcessPhoto(context: Context, imageCapture: ImageCapture) {
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/VehicleAlert")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                // ignore here
            }
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                CoroutineScope(Dispatchers.IO).launch {
                    val uri = outputFileResults.savedUri ?: return@launch
                    val text = OcrUtil.recognizeTextFromUri(context, uri)
                    val plate = PlateUtil.extractLikelyPlate(text) ?: return@launch
                    val normalized = PlateUtil.normalize(plate)

                    val db = AppDatabase.getInstance(context)
                    val vehicle = db.vehicleDao().findByPlate(normalized)
                    if (vehicle != null) {
                        val owner = db.ownerDao().getById(vehicle.ownerId) ?: return@launch
                        val loc = LocationUtil.getCurrentLocation(context)
                        val maps = loc?.let { LocationUtil.mapsLink(it.latitude, it.longitude) } ?: ""
                        val message = "Your vehicle ${'$'}normalized is detected here ${'$'}maps"
                        NotificationsUtil.sendSms(context, owner.phoneNumber, message)
                        owner.whatsappNumber?.let { NotificationsUtil.sendWhatsApp(context, it, message) }
                        // In a real flow, you'd wait for ack; here we can fallback to call
                        NotificationsUtil.callOwner(context, owner.phoneNumber)
                    }
                }
            }
        }
    )
}
