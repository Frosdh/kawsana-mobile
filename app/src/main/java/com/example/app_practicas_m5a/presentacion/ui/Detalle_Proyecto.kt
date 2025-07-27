package com.example.app_practicas_m5a.presentacion.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.InscripcionProyectoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Detalle_Proyecto : AppCompatActivity() {

    private var proyectoId: Long = -1L
    private var usuarioId: Long = -1L
    private var barrioId: Long = -1L

    private lateinit var btnRegistrarse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectos_completos)

        // Recuperar datos del intent
        proyectoId = intent.getLongExtra("proyecto_id", -1L)
        val nombreProyecto = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val fechaInicio = intent.getStringExtra("fecha_inicio")
        val fechaFin = intent.getStringExtra("fecha_fin")
        val nombreOrg = intent.getStringExtra("nombre_org")
        val direccionOrg = intent.getStringExtra("direccion_org")
        val telefonoOrg = intent.getStringExtra("telefono_org")
        val representanteOrg = intent.getStringExtra("representante_org")

        // Recuperar usuario y barrio desde SharedPreferences
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        usuarioId = prefs.getLong("usuario_id", -1L)
        barrioId = prefs.getLong("barrio_id", -1L)

        // Referencias de vistas
        findViewById<TextView>(R.id.tvNombreProyectoDetalle).text = nombreProyecto
        findViewById<TextView>(R.id.tvDescripcionProyectoDetalle).text = descripcion
        findViewById<TextView>(R.id.tvFechaInicio).text = "Fecha de inicio: $fechaInicio"
        findViewById<TextView>(R.id.tvFechaFin).text = "Fecha de fin: $fechaFin"
        findViewById<TextView>(R.id.tvNombreOrganizacion).text = "Organización: $nombreOrg"
        findViewById<TextView>(R.id.tvDireccionOrganizacion).text = "Dirección: $direccionOrg"
        findViewById<TextView>(R.id.tvTelefonoOrganizacion).text = "Teléfono: $telefonoOrg"
        findViewById<TextView>(R.id.tvRepresentanteOrganizacion).text = "Representante: $representanteOrg"

        btnRegistrarse = findViewById(R.id.btnRegistrarseProyecto)

        // Verificar si ya está inscrito en algún proyecto
        CoroutineScope(Dispatchers.IO).launch {
            val proyectoInscrito = InscripcionProyectoDao.obtenerProyectoInscrito(usuarioId)
            withContext(Dispatchers.Main) {
                when {
                    proyectoInscrito == proyectoId -> {
                        // Ya inscrito en este proyecto
                        btnRegistrarse.isEnabled = false
                        btnRegistrarse.text = "Ya inscrito"
                        btnRegistrarse.backgroundTintList = ContextCompat.getColorStateList(
                            this@Detalle_Proyecto, R.color.gray
                        )
                        Toast.makeText(this@Detalle_Proyecto, "Ya estás inscrito en este proyecto.", Toast.LENGTH_LONG).show()
                    }
                    proyectoInscrito != null && proyectoInscrito != proyectoId -> {
                        // Inscrito en otro proyecto
                        btnRegistrarse.isEnabled = false
                        btnRegistrarse.text = "Ya inscrito en otro proyecto"
                        btnRegistrarse.backgroundTintList = ContextCompat.getColorStateList(
                            this@Detalle_Proyecto, R.color.gray
                        )
                        Toast.makeText(this@Detalle_Proyecto, "Ya estás inscrito en otro proyecto.", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        // No inscrito en ningún proyecto, puede registrarse
                        btnRegistrarse.isEnabled = true
                        btnRegistrarse.text = "Registrarse"
                        btnRegistrarse.backgroundTintList = ContextCompat.getColorStateList(
                            this@Detalle_Proyecto, R.color.teal_700
                        )
                    }
                }
            }
        }

        // Listener del botón
        btnRegistrarse.setOnClickListener {
            if (usuarioId == -1L || barrioId == -1L || proyectoId == -1L) {
                Toast.makeText(this, "Error: datos incompletos para registrarse", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Registrando al proyecto...", Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.IO).launch {
                val exito = InscripcionProyectoDao.registrarInscripcion(usuarioId, barrioId, proyectoId)
                withContext(Dispatchers.Main) {
                    if (exito) {
                        Toast.makeText(this@Detalle_Proyecto, "Registro exitoso!", Toast.LENGTH_LONG).show()
                        val resultIntent = Intent()
                        resultIntent.putExtra("proyecto_id", proyectoId)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this@Detalle_Proyecto, "Error al registrar o ya inscrito.", Toast.LENGTH_LONG).show()
                        btnRegistrarse.isEnabled = false
                        btnRegistrarse.text = "Ya inscrito en otro proyecto"
                        btnRegistrarse.backgroundTintList = ContextCompat.getColorStateList(
                            this@Detalle_Proyecto, R.color.gray
                        )
                    }
                }
            }
        }
    }
}
