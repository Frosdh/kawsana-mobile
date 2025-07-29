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
    private lateinit var tvPuntos: TextView
    private lateinit var tvInsignias: TextView

    private lateinit var usuario: String
    private var usuarioId: Long = -1

    private val handler = Handler(Looper.getMainLooper())
    private val slideInterval: Long = 4000

    private val slideRunnable = object : Runnable {
        override fun run() {
            val currentItem = viewPagerNosotros.currentItem
            val totalItems = carruselAdapter.itemCount
            val nextItem = if (currentItem == totalItems - 1) 0 else currentItem + 1
            viewPagerNosotros.setCurrentItem(nextItem, true)
        }
    }

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
        tvPuntos = findViewById(R.id.tvPuntos)
        tvInsignias = findViewById(R.id.tvInsignias)

        usuario = intent.getStringExtra("usuario") ?: ""

        if (usuario.isEmpty()) {
            Toast.makeText(this, "Error: Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosAdministrador()

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

        viewPagerNosotros.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                handler.removeCallbacks(slideRunnable)
                handler.postDelayed(slideRunnable, slideInterval)
            }
        })

        btnVerPerfil.setOnClickListener {
            val intent = Intent(this, Perfil_Admin::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        cardProyectos.setOnClickListener {
            startActivity(Intent(this, Proyectos_Disponibles::class.java))
        }

        cardVerActi.setOnClickListener {
            val intent = Intent(this, Proyecto_Actuales_Lider::class.java)
            intent.putExtra("usuario", usuario)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
        }

        cardActividades.setOnClickListener {
            startActivity(Intent(this, Camara_IA_lider::class.java))
        }

        cardVoluntarios.setOnClickListener {
            val intent = Intent(this, VoluntariosBarrio::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        cardGraficas.setOnClickListener {
            val intent = Intent(this, MostrarGraficoLider::class.java)
            startActivity(intent)
        }

        val btnSalir: Button = findViewById(R.id.btnSalir)
        btnSalir.setOnClickListener {
            val intent = Intent(this, Login::class.java)
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

                val puntos = withContext(Dispatchers.IO) {
                    CoreUsuarioDao.obtenerPuntosPorUsuario(usuario)
                }

                val puntosVal = puntos ?: 0
                calcularInsigniaYProgreso(puntosVal, 100)

            } else {
                Toast.makeText(
                    this@Pagina_principal_adm,
                    "No se encontró el administrador",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun calcularInsigniaYProgreso(puntos: Int, maximo: Int) {
        val porcentaje = calcularPorcentaje(puntos, maximo)
        val insignia = when (porcentaje) {
            in 1..19 -> "🥉 Novato"
            in 20..39 -> "🥈 Avanzado"
            in 40..59 -> "🥇 Experto"
            in 60..79 -> "🏅 Líder"
            in 80..100 -> "🏆 Leyenda"
            else -> "🔰 Sin insignia"
        }

        tvPuntos.text = "🔥 Puntos: $puntos"
        tvInsignias.text = insignia
    }

    private fun calcularPorcentaje(puntos: Int, maximo: Int): Int {
        if (maximo <= 0) return 0
        val porcentaje = (puntos.toFloat() * 100f) / maximo.toFloat()
        return porcentaje.coerceAtMost(100f).toInt()
    }
}
