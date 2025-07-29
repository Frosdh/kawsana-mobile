package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.CarruselAdapter
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.dao.ProyectoDisponobleDao
import com.example.app_practicas_m5a.data.model.Proyectos
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_adm : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var btnVerPerfil: ImageView
    private lateinit var cardProyectos: LinearLayout
    private lateinit var cardVerActi: LinearLayout
    private lateinit var cardActividades: LinearLayout
    private lateinit var cardVoluntarios: LinearLayout
    private lateinit var cardGraficas: LinearLayout

    private lateinit var viewPagerNosotros: ViewPager2
    private lateinit var carruselAdapter: CarruselAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val slideInterval: Long = 4000 // 4 segundos

    private val slideRunnable = object : Runnable {
        override fun run() {
            val currentItem = viewPagerNosotros.currentItem
            val totalItems = carruselAdapter.itemCount
            val nextItem = if (currentItem == totalItems - 1) 0 else currentItem + 1
            viewPagerNosotros.setCurrentItem(nextItem, true)
            // No programar postDelayed aquí para evitar acumulaciones
        }
    }

    private lateinit var usuario: String
    private var usuarioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_adm)

        // Vincular UI
        tvSaludo = findViewById(R.id.tvSaludo)
        btnVerPerfil = findViewById(R.id.imgPerfil)
        cardProyectos = findViewById(R.id.cardProyectos)
        cardVerActi = findViewById(R.id.cardVerActi)
        cardActividades = findViewById(R.id.cardActividades)
        cardVoluntarios = findViewById(R.id.cardVoluntarios)
        cardGraficas = findViewById(R.id.cardGraficas)
        viewPagerNosotros = findViewById(R.id.viewPagerNosotros)

        // Recibir datos del login
        usuario = intent.getStringExtra("usuario") ?: ""

        if (usuario.isEmpty()) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosAdministrador()

        // Imagenes carrusel
        val imagenesCarrusel = listOf(
            R.drawable.wendy,
            R.drawable.adri,
            R.drawable.erick,
            R.drawable.edwin,
            R.drawable.kenny,
            R.drawable.steven
        )
        carruselAdapter = CarruselAdapter(imagenesCarrusel)
        viewPagerNosotros.adapter = carruselAdapter

        // Registrar callback para reiniciar temporizador al cambiar página
        viewPagerNosotros.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                handler.removeCallbacks(slideRunnable)
                handler.postDelayed(slideRunnable, slideInterval)
            }
        })

        // Botón ver perfil
        btnVerPerfil.setOnClickListener {
            val intent = Intent(this, Perfil_Admin::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        // Botón ver proyectos
        cardProyectos.setOnClickListener {
            startActivity(Intent(this, Proyectos_Disponibles::class.java))
        }

        // Botón proyectos actuales del líder
        cardVerActi.setOnClickListener {
            val intent = Intent(this, Proyecto_Actuales_Lider::class.java)
            intent.putExtra("usuario", usuario)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
        }

        // Botón cámara IA
        cardActividades.setOnClickListener {
            startActivity(Intent(this, Camara_IA_lider::class.java))
        }

        // Botón ver voluntarios
        cardVoluntarios.setOnClickListener {
            val intent = Intent(this, VoluntariosBarrio::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }
        // Listener: Actividades
        cardGraficas.setOnClickListener {
            val intent = Intent(this, MostrarGraficoLider::class.java)
            startActivity(intent)
        }

        val btnSalir: Button = findViewById(R.id.btnSalir)
        btnSalir.setOnClickListener {
            val intent = Intent(this, Login::class.java) // Cambia por el nombre de tu actividad de login si es diferente
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInicioAdmin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(slideRunnable, slideInterval)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(slideRunnable)
    }

    private fun cargarDatosAdministrador() {
        lifecycleScope.launch {
            val admin = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorUsuario(usuario)
            }

            if (admin != null) {
                usuarioId = admin.id
                tvSaludo.text = "👋 Bienvenido, ${admin.nombres} ${admin.apellidos}"
            } else {
                Toast.makeText(
                    this@Pagina_principal_adm,
                    "No se encontró el administrador",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}