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
import com.example.app_practicas_m5a.data.dao.InfoVoluntarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_vol : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvPuntos: TextView
    private lateinit var tvInsignias: TextView
    private lateinit var tvActividades: TextView
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
        tvNoticias = findViewById(R.id.tvNoticias)
        tvAvance = findViewById(R.id.tvAvance)
        progreso = findViewById(R.id.progressBarAvance)

        val cedula = intent.getStringExtra("cedula") ?: return

        // Corrutina para cargar datos del voluntario desde DAO
        lifecycleScope.launch {
            val info = withContext(Dispatchers.IO) {
                InfoVoluntarioDao.obtenerInfoPorCedula(cedula)
            }

            info?.let {
                tvSaludo.text = "👋 Hola, ${it.nombreCompleto}"
                tvPuntos.text = "🔥 Puntos: ${it.puntosTotales} pts"
                tvInsignias.text = "🏅 Insignias: ${it.insignias.joinToString(" ")}"
                tvActividades.text = it.proximasActividades.joinToString("\n") { actividad -> "- $actividad" }
                tvNoticias.text = it.noticias.joinToString("\n") { noticia -> "- $noticia" }
                tvAvance.text = "${String.format("%.1f", it.avancePorcentaje)}%"
                progreso.progress = it.avancePorcentaje.toInt()
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
