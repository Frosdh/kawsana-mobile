package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.ActividadDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Proyecto_Actuales_Lider : AppCompatActivity() {

    private lateinit var layoutProyectos: LinearLayout
    private lateinit var layoutProyectosSubidos: LinearLayout

    private var usuarioId: Long = -1
    private lateinit var usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyecto_actuales_lider)

        layoutProyectos = findViewById(R.id.layoutProyectos)
        layoutProyectosSubidos = findViewById(R.id.layoutProyectosSubidos)

        usuario = intent.getStringExtra("usuario") ?: return
        usuarioId = intent.getLongExtra("usuario_id", -1)

        cargarActividades(usuario)
    }

    private fun cargarActividades(usuario: String) {
        lifecycleScope.launch {
            val pendientes = withContext(Dispatchers.IO) {
                ActividadDao.obtenerActividadesSinEvidencia(usuario)
            }
            val subidas = withContext(Dispatchers.IO) {
                ActividadDao.obtenerActividadesConEvidencia(usuario)
            }

            layoutProyectos.removeAllViews()
            layoutProyectosSubidos.removeAllViews()

            // ✅ ACTIVIDADES PENDIENTES
            if (pendientes.isEmpty()) {
                val txtPendientes = TextView(this@Proyecto_Actuales_Lider).apply {
                    text = "No tienes actividades pendientes por subir"
                    textSize = 16f
                    setPadding(24, 24, 24, 24)
                }
                layoutProyectos.addView(txtPendientes)
            } else {
                pendientes.forEach { actividad ->
                    val cardView = layoutInflater.inflate(R.layout.item_actividad_lider, layoutProyectos, false)

                    val tvNombre = cardView.findViewById<TextView>(R.id.tvNombreActividad)
                    val tvDescripcion = cardView.findViewById<TextView>(R.id.tvDescripcionActividad)
                    val tvFechas = cardView.findViewById<TextView>(R.id.tvFechasActividad)
                    val tvPuntos = cardView.findViewById<TextView>(R.id.tvPuntosActividad)

                    tvNombre.text = actividad.nombre
                    tvDescripcion.text = actividad.descripcion ?: "Sin descripción"
                    tvFechas.text = "📅 ${actividad.fechaInicio} - ${actividad.fechaFin ?: "Sin fecha fin"}"
                    tvPuntos.text = "⭐ Puntos: ${actividad.puntos}"

                    cardView.setOnClickListener {
                        val intent = Intent(this@Proyecto_Actuales_Lider, Subir_Actividades_Proyecto::class.java)
                        intent.putExtra("actividad_id", actividad.id)
                        intent.putExtra("proyecto_id", actividad.proyectoId)
                        intent.putExtra("usuario_id", usuarioId)
                        startActivityForResult(intent, 200)
                    }

                    layoutProyectos.addView(cardView)
                }
            }

            // ✅ ACTIVIDADES YA SUBIDAS
            if (subidas.isEmpty()) {
                val txtSubidas = TextView(this@Proyecto_Actuales_Lider).apply {
                    text = "No tienes actividades ya subidas"
                    textSize = 16f
                    setPadding(24, 24, 24, 24)
                }
                layoutProyectosSubidos.addView(txtSubidas)
            } else {
                subidas.forEach { actividad ->
                    val cardView = layoutInflater.inflate(R.layout.item_actividad_lider, layoutProyectosSubidos, false)

                    val tvNombre = cardView.findViewById<TextView>(R.id.tvNombreActividad)
                    val tvDescripcion = cardView.findViewById<TextView>(R.id.tvDescripcionActividad)
                    val tvFechas = cardView.findViewById<TextView>(R.id.tvFechasActividad)
                    val tvPuntos = cardView.findViewById<TextView>(R.id.tvPuntosActividad)

                    tvNombre.text = actividad.nombre
                    tvDescripcion.text = actividad.descripcion ?: "Sin descripción"
                    tvFechas.text = "📅 ${actividad.fechaInicio} - ${actividad.fechaFin ?: "Sin fecha fin"}"
                    tvPuntos.text = "⭐ Puntos: ${actividad.puntos}"

                    cardView.setOnClickListener {
                        Toast.makeText(this@Proyecto_Actuales_Lider, "La actividad ya se subió", Toast.LENGTH_SHORT).show()
                    }

                    layoutProyectosSubidos.addView(cardView)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == RESULT_OK) {
            cargarActividades(usuario)
        }
    }
}
