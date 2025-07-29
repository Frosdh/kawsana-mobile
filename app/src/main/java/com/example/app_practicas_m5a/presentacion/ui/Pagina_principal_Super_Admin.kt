package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.CarruselAdapter
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_Super_Admin : AppCompatActivity() {


    private lateinit var imgPerfilSuper: ImageView
    private lateinit var tvsaludo: TextView

    // Cards
    private lateinit var cardOrganizaciones: LinearLayout
    private lateinit var cardLideres: LinearLayout
    private lateinit var cardActividades: LinearLayout
    private lateinit var cardGraficas: LinearLayout
    private lateinit var viewPagerNosotros: ViewPager2
    private lateinit var carruselAdapter: CarruselAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val slideInterval: Long = 4000 // 4 segundos


    private lateinit var usuario: String

    private val slideRunnable = object : Runnable {
        override fun run() {
            val currentItem = viewPagerNosotros.currentItem
            val totalItems = carruselAdapter.itemCount
            val nextItem = if (currentItem == totalItems - 1) 0 else currentItem + 1
            viewPagerNosotros.setCurrentItem(nextItem, true)
            // No programar postDelayed aquí para evitar acumulaciones
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_super_admin)


        imgPerfilSuper = findViewById(R.id.imgPerfilSuper)
        tvsaludo = findViewById(R.id.tvSaludo)


        // Cards
        cardOrganizaciones = findViewById(R.id.cardOrganizaciones)
        cardLideres = findViewById(R.id.cardlideres)
        cardActividades = findViewById(R.id.cardActividades)
        cardGraficas = findViewById(R.id.cardGraficas)
        viewPagerNosotros = findViewById(R.id.viewPagerNosotros)
        usuario = intent.getStringExtra("usuario") ?: ""

        // Cargar datos del super admin
        lifecycleScope.launch {
            val admin = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorUsuario(usuario)
            }

            if (admin != null) {
                tvsaludo.text = "👋 Bienvenido, ${admin.nombres} ${admin.apellidos}"
            }
        }
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
        val btnSalir: Button = findViewById(R.id.btnSalir)
        btnSalir.setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java
            ) // Cambia por el nombre de tu actividad de login si es diferente
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        imgPerfilSuper.setOnClickListener {
            val intent = Intent(this, Perfil_Super_Admin::class.java)
            intent.putExtra("usuario", usuario) // Envía el nombre de usuario actual
            startActivity(intent)
        }

        // Navegación a cada módulo
        cardOrganizaciones.setOnClickListener {
            startActivity(Intent(this, OrganizacionesActivity::class.java).apply {
                putExtra("usuario", usuario)
            })
        }

        cardLideres.setOnClickListener {
            startActivity(Intent(this, LideresActivity::class.java).apply {
                putExtra("usuario", usuario)
            })
        }

        cardActividades.setOnClickListener {
            startActivity(Intent(this, ActividadesActivityAdmin::class.java).apply {
                putExtra("usuario", usuario)
            })
        }

        cardGraficas.setOnClickListener {
            startActivity(Intent(this, GraficasActivityAdmin::class.java).apply {
                putExtra("usuario", usuario)
            })
        }
    }
}