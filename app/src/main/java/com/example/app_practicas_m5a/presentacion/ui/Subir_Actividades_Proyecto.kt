package com.example.app_practicas_m5a.presentacion.ui

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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

class Subir_Actividades_Proyecto : AppCompatActivity() {

    private lateinit var imgEvidencia: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var etDescripcion: EditText
    private lateinit var etFecha: EditText
    private lateinit var btnSubir: Button

    private var imagenUri: Uri? = null
    private var uriFoto: Uri? = null

    private val SELECT_IMAGE_REQUEST = 1010
    private val CAMERA_IMAGE_REQUEST = 1011
    private val REQUEST_PERMISSIONS = 100

    private var actividadId: Long = -1
    private var usuarioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_actividades_proyecto)

        imgEvidencia = findViewById(R.id.imgEvidencia)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        etDescripcion = findViewById(R.id.etDescripcion)
        etFecha = findViewById(R.id.etFecha)
        btnSubir = findViewById(R.id.btnSubir)

        actividadId = intent.getLongExtra("actividad_id", -1)
        usuarioId = intent.getLongExtra("usuario_id", -1)

        println("DEBUG -> actividadId: $actividadId | usuarioId: $usuarioId")

        btnSeleccionarImagen.setOnClickListener {
            if (tienePermisos()) {
                mostrarOpcionesSeleccion()
            } else {
                pedirPermisos()
            }
        }

        etFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(
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
            val descripcion = etDescripcion.text.toString().trim()
            val fecha = etFecha.text.toString().trim()

            if (descripcion.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imagenUri == null) {
                Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (actividadId == -1L) {
                Toast.makeText(this, "No se recibió el ID de la actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuarioId == -1L) {
                Toast.makeText(this, "No se recibió el ID del usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val evidencia = EvidenciaActividad(
                archivoUrl = imagenUri.toString(),
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
                        Toast.makeText(this@Subir_Actividades_Proyecto, "Error al subir actividad", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun tienePermisos(): Boolean {
        val permisoCamara = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val permisoLectura = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        return permisoCamara && permisoLectura
    }

    private fun pedirPermisos() {
        val permisos = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(this, permisos.toTypedArray(), REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                mostrarOpcionesSeleccion()
            } else {
                Toast.makeText(this, "Debe otorgar permisos para continuar", Toast.LENGTH_LONG).show()

                val denegadosPermanentemente = permissions.any {
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, it)
                }

                if (denegadosPermanentemente) {
                    AlertDialog.Builder(this)
                        .setTitle("Permisos necesarios")
                        .setMessage("Has denegado los permisos permanentemente. Ve a ajustes para activarlos.")
                        .setPositiveButton("Ir a Ajustes") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:$packageName")
                            }
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }
        }
    }

    private fun mostrarOpcionesSeleccion() {
        val opciones = arrayOf("Seleccionar desde galería", "Tomar foto con cámara")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> seleccionarDesdeGaleria()
                    1 -> tomarFotoConCamara()
                }
            }
            .show()
    }

    private fun seleccionarDesdeGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, SELECT_IMAGE_REQUEST)
    }

    private fun tomarFotoConCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val archivoFoto = crearArchivoImagen()
        uriFoto = FileProvider.getUriForFile(this, "${packageName}.provider", archivoFoto)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto)

        // Agregar flags para otorgar permisos temporales al URI
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Verificar que exista app cámara para manejar el intent
        val resuelve = intent.resolveActivity(packageManager)
        if (resuelve != null) {
            // Otorgar permisos explícitos a la app de cámara
            grantUriPermission(resuelve.packageName, uriFoto, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST)
        } else {
            Toast.makeText(this, "No se encontró aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }


    private fun crearArchivoImagen(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreArchivo = "EVID_${timestamp}_"
        val directorio = getExternalFilesDir("Pictures")
        return File.createTempFile(nombreArchivo, ".jpg", directorio)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            imagenUri = when (requestCode) {
                SELECT_IMAGE_REQUEST -> data?.data
                CAMERA_IMAGE_REQUEST -> uriFoto
                else -> null
            }

            imagenUri?.let {
                val inputStream: InputStream? = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imgEvidencia.setImageBitmap(bitmap)
            }
        }
    }
}
