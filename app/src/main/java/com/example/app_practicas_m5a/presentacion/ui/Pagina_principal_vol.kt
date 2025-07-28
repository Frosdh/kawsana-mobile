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
import com.example.app_practicas_m5a.data.dao.BarrioDaoProgresoGrafico
import com.example.app_practicas_m5a.data.dao.ProyectosDispoVoluntarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_vol : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvPuntos: TextView
    private lateinit var tvInsignias: TextView
    private lateinit var tvAvance: TextView
    private lateinit var progreso: ProgressBar
    private lateinit var cardActividades: LinearLayout
    private lateinit var cardProyectos: LinearLayout
    private lateinit var usuario: String
    private lateinit var btnPerfil: ImageView
    private lateinit var nombreUsuario: String
    private lateinit var cardGraficas: LinearLayout
    private lateinit var cardNoticias: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_vol)

        // Referencias UI
        tvSaludo = findViewById(R.id.tvSaludo)
        tvPuntos = findViewById(R.id.tvPuntos)
        tvInsignias = findViewById(R.id.tvInsignias)
        tvAvance = findViewById(R.id.tvAvance)
        progreso = findViewById(R.id.progressBarAvance)
        cardProyectos = findViewById(R.id.cardProyectos)
        cardActividades = findViewById(R.id.cardActividades)
        btnPerfil = findViewById<ImageView>(R.id.btnPerfil)
        cardNoticias = findViewById<LinearLayout>(R.id.cardNoticias)
        cardGraficas = findViewById(R.id.cardGraficas)

        // Obtener datos del intent
        usuario = intent.getStringExtra("usuario") ?: "No disponible"
        nombreUsuario = intent.getStringExtra("nombre") ?: "Usuario"

        // Mostrar saludo personalizado
        tvSaludo.text = "👋 Bienvenido, $nombreUsuario"

        // Listeners
        cardActividades.setOnClickListener {
            val intent = Intent(this, Camara_IA_voluntario::class.java)
            startActivity(intent)
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Perfil_voluntario::class.java)
            intent.putExtra("usuario", usuario)
            intent.putExtra("nombre", nombreUsuario)
            startActivity(intent)
        }

        cardProyectos.setOnClickListener {
            val intent = Intent(this, Proyectos_Disponibles_Vol::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val puntosTotales = withContext(Dispatchers.IO) {
                ProyectosDispoVoluntarioDao.obtenerPuntosTotalesPorUsuario(usuario)
            }
            // Aquí defines tu máximo esperado de puntos (puedes cambiarlo)
            val maximoPuntos = 1000
            calcularInsigniaYProgreso(puntosTotales, maximoPuntos)
        }

        cardGraficas.setOnClickListener {
            lifecycleScope.launch {
                val progresoBarrios = withContext(Dispatchers.IO) {
                    BarrioDaoProgresoGrafico.obtenerBarriosOrdenadosPorProgreso()
                }

                val nombres = ArrayList(progresoBarrios.map { it.nombreBarrio })
                val valores = progresoBarrios.map { it.progreso.toFloat() }.toFloatArray()

                val intent = Intent(this@Pagina_principal_vol, MostrarGraficoActivity::class.java)
                intent.putStringArrayListExtra("nombres", nombres)
                intent.putExtra("valores", valores)
                startActivity(intent)
            }
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

    private fun calcularInsigniaYProgreso(puntos: Int, maximo: Int) {
        val puntosEscalados = calcularPorcentaje(puntos, maximo)

        val insignia = when (puntosEscalados) {
            in 1..19 -> "🥉 Novato"
            in 20..39 -> "🥈 Avanzado"
            in 40..59 -> "🥇 Experto"
            in 60..79 -> "🏅 Líder"
            in 80..100 -> "🏆 Leyenda"
            else -> "🔰 Sin insignia"
        }

        tvPuntos.text = "🔥 Puntos reales: $puntos"
        tvInsignias.text = insignia
        tvAvance.text = "$puntosEscalados%"
        progreso.progress = puntosEscalados
    }

    private fun calcularPorcentaje(puntos: Int, maximo: Int): Int {
        if (maximo <= 0) return 0
        val porcentaje = (puntos.toFloat() * 100f) / maximo.toFloat()
        return porcentaje.coerceAtMost(100f).toInt()
    }

}
