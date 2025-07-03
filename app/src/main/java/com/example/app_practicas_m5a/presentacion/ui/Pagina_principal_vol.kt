package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.ActiDispoVoluntarioDao
import com.example.app_practicas_m5a.data.dao.ProyectosDispoVoluntarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_vol : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvPuntos: TextView
    private lateinit var tvInsignias: TextView
    private lateinit var tvActividades: TextView
    private lateinit var tvProyectosDisponibles: TextView
    private lateinit var tvNoticias: TextView
    private lateinit var tvAvance: TextView
    private lateinit var progreso: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_vol)

        // Referencias UI
        tvSaludo = findViewById(R.id.tvSaludo)
        tvPuntos = findViewById(R.id.tvPuntos)
        tvInsignias = findViewById(R.id.tvInsignias)
        tvActividades = findViewById(R.id.tvActividades)
        tvProyectosDisponibles = findViewById(R.id.tvProyectosDisponibles)
        tvNoticias = findViewById(R.id.tvNoticias)
        tvAvance = findViewById(R.id.tvAvance)
        progreso = findViewById(R.id.progressBarAvance)

        tvActividades.text = "Cargando actividades..."
        tvProyectosDisponibles.text = "Cargando proyectos..."

        // Puedes recibir cedula si la usas, pero no es obligatoria para mostrar
        val cedula = intent.getStringExtra("cedula")

        lifecycleScope.launch {
            // Cargar actividades en background
            val actividades = withContext(Dispatchers.IO) {
                ActiDispoVoluntarioDao.obtenerActividadesActivas()
            }
            if (actividades.isNotEmpty()) {
                val textoActividades = actividades.joinToString("\n\n") { actividad ->
                    """
                    📌 ${actividad.nombre}
                    🗓️ Desde: ${actividad.fecha_inicio}
                    📅 Hasta: ${actividad.fecha_fin ?: "No especificada"}
                    ⭐ Puntos: ${actividad.puntos}
                    """.trimIndent()
                }
                tvActividades.text = textoActividades
            } else {
                tvActividades.text = "No hay actividades disponibles por el momento."
            }
        }

        lifecycleScope.launch {
            // Cargar proyectos en background
            val proyectos = withContext(Dispatchers.IO) {
                ProyectosDispoVoluntarioDao.obtenerProyectosActivos()
            }
            if (proyectos.isNotEmpty()) {
                val textoProyectos = proyectos.joinToString("\n\n") { proyecto ->
                    """
                    🏗️ ${proyecto.nombre}
                    🗓️ Desde: ${proyecto.fecha_inicio}
                    📅 Hasta: ${proyecto.fecha_fin ?: "No especificada"}
                    📋 Descripción: ${proyecto.descripcion ?: "Sin descripción"}
                    """.trimIndent()
                }
                tvProyectosDisponibles.text = textoProyectos
            } else {
                tvProyectosDisponibles.text = "No hay proyectos disponibles por el momento."
            }
        }

        // Ajuste de paddings por barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
