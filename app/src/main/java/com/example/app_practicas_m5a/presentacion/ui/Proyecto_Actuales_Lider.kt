package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.ActividadDao
import com.example.app_practicas_m5a.data.model.ActividadModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Proyecto_Actuales_Lider : AppCompatActivity() {

    private lateinit var layoutPendientes: LinearLayout
    private lateinit var layoutSubidasPendientes: LinearLayout
    private lateinit var layoutAprobadas: LinearLayout

    private var usuarioId: Long = -1
    private lateinit var usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyecto_actuales_lider)

        layoutPendientes = findViewById(R.id.layoutPendientes)
        layoutSubidasPendientes = findViewById(R.id.layoutSubidasPendientes)
        layoutAprobadas = findViewById(R.id.layoutAprobadas)

        usuario = intent.getStringExtra("usuario") ?: return
        usuarioId = intent.getLongExtra("usuario_id", -1)

        cargarActividades()
    }

    private fun cargarActividades() {
        lifecycleScope.launch {
            val sinEvidencia = withContext(Dispatchers.IO) {
                ActividadDao.obtenerActividadesSinEvidencia(usuarioId)
            }
            val evidenciaPendiente = withContext(Dispatchers.IO) {
                ActividadDao.obtenerActividadesConEvidenciaPendiente(usuarioId)
            }
            val aprobadas = withContext(Dispatchers.IO) {
                ActividadDao.obtenerActividadesAprobadas(usuarioId)
            }

            layoutPendientes.removeAllViews()
            layoutSubidasPendientes.removeAllViews()
            layoutAprobadas.removeAllViews()

            mostrarActividades(
                sinEvidencia,
                layoutPendientes,
                "No hay actividades por subir"
            ) { abrirFormularioSubir(it) }

            mostrarActividades(
                evidenciaPendiente,
                layoutSubidasPendientes,
                "No hay actividades pendientes"
            ) {
                Toast.makeText(this@Proyecto_Actuales_Lider, "Evidencia en espera de validación", Toast.LENGTH_SHORT).show()
            }

            mostrarActividades(
                aprobadas,
                layoutAprobadas,
                "No hay actividades finalizadas"
            ) {
                Toast.makeText(this@Proyecto_Actuales_Lider, "Actividad finalizada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarActividades(
        lista: List<ActividadModel>,
        layout: LinearLayout,
        mensajeVacio: String,
        onClick: (ActividadModel) -> Unit
    ) {
        layout.removeAllViews()

        if (lista.isEmpty()) {
            val txt = TextView(this).apply {
                text = mensajeVacio
                textSize = 16f
                setPadding(24, 24, 24, 24)
            }
            layout.addView(txt)
            return
        }

        lista.forEach { actividad ->
            val view = layoutInflater.inflate(R.layout.item_actividad_lider, layout, false)

            val tvNombre = view.findViewById<TextView>(R.id.tvNombreActividad)
            val tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcionActividad)
            val tvFechas = view.findViewById<TextView>(R.id.tvFechasActividad)
            val tvPuntos = view.findViewById<TextView>(R.id.tvPuntosActividad)

            if (tvNombre == null || tvDescripcion == null || tvFechas == null || tvPuntos == null) {
                Log.e("Proyecto_Actuales_Lider", "Error: algún TextView es null en item_actividad_lider.xml")
                return@forEach
            }

            tvNombre.text = actividad.nombre
            tvDescripcion.text = actividad.descripcion ?: "Sin descripción"
            val fechaInicio = actividad.fechaInicio?.toString() ?: "Sin fecha inicio"
            val fechaFin = actividad.fechaFin?.toString() ?: "Sin fecha fin"
            tvFechas.text = "📅 $fechaInicio - $fechaFin"
            tvPuntos.text = "⭐ Puntos: ${actividad.puntos}"

            view.setOnClickListener { onClick(actividad) }
            layout.addView(view)
        }
    }

    private fun abrirFormularioSubir(actividad: ActividadModel) {
        val intent = Intent(this, Subir_Actividades_Proyecto::class.java)
        intent.putExtra("actividad_id", actividad.id)
        intent.putExtra("proyecto_id", actividad.proyectoId)
        intent.putExtra("usuario_id", usuarioId)
        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == RESULT_OK) {
            cargarActividades()
        }
    }
}
