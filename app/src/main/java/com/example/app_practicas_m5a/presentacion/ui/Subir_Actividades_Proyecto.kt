package com.example.app_practicas_m5a.presentacion.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.EvidenciaDao
import com.example.app_practicas_m5a.data.model.EvidenciaActividad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Subir_Actividades_Proyecto : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var btnCapturarFoto: Button
    private lateinit var imgEvidencia: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var etDescripcion: EditText
    private lateinit var etFecha: EditText
    private lateinit var btnSubir: Button


    private var imagenUri: Uri? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    private var actividadId: Long = -1
    private var usuarioId: Long = -1

    private val REQUEST_PERMISSIONS = 100
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_actividades_proyecto)

        previewView = findViewById(R.id.previewView)
        btnCapturarFoto = findViewById(R.id.btnCapturarFoto)
        imgEvidencia = findViewById(R.id.imgEvidencia)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        etDescripcion = findViewById(R.id.etDescripcion)
        etFecha = findViewById(R.id.etFecha)
        btnSubir = findViewById(R.id.btnSubir)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        actividadId = intent.getLongExtra("actividad_id", -1)
        usuarioId = intent.getLongExtra("usuario_id", -1)

        // Verificar permisos y abrir cámara al iniciar
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS)
        }

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1012)
        }

        btnCapturarFoto.setOnClickListener {
            takePhoto()
        }

        etFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    etFecha.setText(fechaSeleccionada)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSubir.setOnClickListener {
            subirActividad()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(windowManager.defaultDisplay.rotation)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Toast.makeText(this, "Error iniciando cámara", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = File(outputDirectory, SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(this@Subir_Actividades_Proyecto, "Error al guardar foto", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imagenUri = Uri.fromFile(photoFile)
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    imgEvidencia.setImageBitmap(bitmap)
                    Toast.makeText(this@Subir_Actividades_Proyecto, "Foto capturada", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun subirActividad() {
        val descripcion = etDescripcion.text.toString().trim()
        val fecha = etFecha.text.toString().trim()

        if (descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (imagenUri == null) {
            Toast.makeText(this, "Debe tomar una foto o seleccionar una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val imagenBase64 = convertirImagenABase64(imagenUri!!)

        val evidencia = EvidenciaActividad(
            archivoUrl = imagenBase64,
            tipoArchivo = "jpg",
            descripcion = descripcion,
            fechaSubida = fecha,
            actividadId = actividadId,
            usuarioId = usuarioId,
            esValida = false,
            fechaValidacion = null,
            validadorId = null
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val resultado = EvidenciaDao.insertarEvidencia(evidencia)
            runOnUiThread {
                if (resultado) {
                    Toast.makeText(this@Subir_Actividades_Proyecto, "Subida exitosa 🎉", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@Subir_Actividades_Proyecto, "Error al subir", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun convertirImagenABase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream) // Calidad 75%
        val byteArray = outputStream.toByteArray()

        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }



    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS && allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
