package com.example.app_practicas_m5a.presentacion.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.app_practicas_m5a.databinding.ActivityCamaraIaLiderBinding
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Camara_IA_lider : AppCompatActivity() {

    private lateinit var binding: ActivityCamaraIaLiderBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var bitmap: Bitmap? = null
    private lateinit var clasificacion: Clasificacion_IA
    private val inputSize = 224

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(it)
            bitmap = BitmapFactory.decodeStream(inputStream)
            binding.imageView.setImageBitmap(bitmap)
            binding.btnPredict.isEnabled = true
            binding.tvResult.text = "Imagen lista para predecir"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCamaraIaLiderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clasificacion = Clasificacion_IA(this)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnCapture.setOnClickListener { takePhoto() }
        binding.btnSelectImage.setOnClickListener { selectImageLauncher.launch("image/*") }

        binding.btnPredict.setOnClickListener {
            bitmap?.let {
                val result = clasificacion.runModel(it, inputSize)
                binding.tvResult.text = "Resultado:\n$result"
            } ?: run {
                binding.tvResult.text = "Primero toma o selecciona una imagen."
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e(TAG, "Error al iniciar la cámara", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmapBuffer = imageToBitmap(image)
                    bitmap = bitmapBuffer
                    runOnUiThread {
                        binding.imageView.setImageBitmap(bitmap)
                        binding.btnPredict.isEnabled = true
                        binding.tvResult.text = "Foto lista para predecir"
                    }
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(baseContext, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun imageToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CamaraIA_Lider"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
