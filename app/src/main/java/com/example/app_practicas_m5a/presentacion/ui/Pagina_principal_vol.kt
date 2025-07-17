package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import android.graphics.Color
import com.example.app_practicas_m5a.data.dao.BarrioDaoProgresoGrafico
import com.example.app_practicas_m5a.data.dao.NoticiaDao
import com.example.app_practicas_m5a.data.dao.ProyectosDispoVoluntarioDao
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_vol : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvPuntos: TextView
    private lateinit var tvInsignias: TextView
    private lateinit var tvNoticias: TextView
    private lateinit var tvAvance: TextView
    private lateinit var progreso: ProgressBar
    private lateinit var cardActividades: LinearLayout
    private lateinit var cardProyectos: LinearLayout
    private lateinit var cedula: String
    private lateinit var nombreUsuario: String  // <- Agregamos esto
    private lateinit var btnPerfil: ImageButton
    private lateinit var barChart: BarChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_vol)


        // Referencias UI
        tvSaludo = findViewById(R.id.tvSaludo)
        tvPuntos = findViewById(R.id.tvPuntos)
        tvInsignias = findViewById(R.id.tvInsignias)
        tvNoticias = findViewById(R.id.tvNoticias)
        tvAvance = findViewById(R.id.tvAvance)
        progreso = findViewById(R.id.progressBarAvance)
        cardProyectos = findViewById(R.id.cardProyectos)
        cardActividades = findViewById(R.id.cardActividades)
        btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val cardNoticias = findViewById<LinearLayout>(R.id.cardNoticias)
        barChart = findViewById(R.id.chartBarrio)


        // Obtener datos del intent
        cedula = intent.getStringExtra("cedula") ?: "No disponible"
        nombreUsuario = intent.getStringExtra("nombre") ?: "Usuario"
        // Mostrar saludo personalizado
        tvSaludo.text = "👋 Bienvenido $nombreUsuario"

        // Listener: Actividades
        cardActividades.setOnClickListener {
            val intent = Intent(this, Actividades_Disponibles_Vol::class.java)
            intent.putExtra("cedula", cedula)
            startActivity(intent)
        }

        // Listener: Proyectos
        cardProyectos.setOnClickListener {
            val intent = Intent(this, Proyectos_Disponibles_Vol::class.java)
            intent.putExtra("cedula", cedula)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Perfil_voluntario::class.java)
            intent.putExtra("cedula", cedula)
            intent.putExtra("nombre", nombreUsuario)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val puntosTotales = withContext(Dispatchers.IO) {
                ProyectosDispoVoluntarioDao.obtenerPuntosTotalesPorCedula(cedula)
            }

            calcularInsigniaYProgreso(puntosTotales)

            // Cargar datos reales para gráfico desde DAO (en IO)
            val progresoBarrios = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerProgresoPorBarrio()
            }

            // Mapear a List<Pair<String, Float>> para el gráfico
            val datosParaGrafico = progresoBarrios.map { it.nombreBarrio to it.progreso.toFloat() }

            // Mostrar gráfico en UI (Main thread)
            mostrarGrafico(datosParaGrafico)
        }
        cardNoticias.setOnClickListener {
            val intent = Intent(this, NoticiasActivity::class.java)
            startActivity(intent)
        }



        // Ajustar padding por las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun calcularInsigniaYProgreso(puntos: Int) {
        val insignia = when (puntos) {
            in 1..19 -> "🥉 Novato"
            in 20..39 -> "🥈 Avanzado"
            in 40..59 -> "🥇 Experto"
            in 60..79 -> "🏅 Líder"
            in 80..100 -> "🏆 Leyenda"
            else -> "🔰 Sin insignia"
        }

        val progresoPorcentaje = if (puntos > 100) 100 else puntos

        tvPuntos.text = "🔥 Puntos: $puntos"
        tvInsignias.text = insignia
        tvAvance.text = "$progresoPorcentaje%"
        progreso.progress = progresoPorcentaje
    }
    private fun mostrarGrafico(datos: List<Pair<String, Float>>) {
        val entradas = datos.mapIndexed { index, par -> BarEntry(index.toFloat(), par.second) }

        val dataSet = BarDataSet(entradas, "Progreso por Barrio").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#81C784"),
                Color.parseColor("#388E3C"),
                Color.parseColor("#A5D6A7")
            )
            valueTextColor = Color.DKGRAY
            valueTextSize = 14f
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        barChart.apply {
            data = barData
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(datos.map { it.first })
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
            }
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 10f
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = true
            animateY(1000)
            invalidate()
        }
    }

}
