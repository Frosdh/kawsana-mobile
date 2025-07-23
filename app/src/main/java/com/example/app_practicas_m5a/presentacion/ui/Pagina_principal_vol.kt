package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_practicas_m5a.R

class Pagina_principal_vol : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvPuntos: TextView
    private lateinit var tvInsignias: TextView
    private lateinit var tvNoticias: TextView
    private lateinit var tvAvance: TextView
    private lateinit var progreso: ProgressBar
    private lateinit var cardActividades: LinearLayout
    private lateinit var cardProyectos: LinearLayout
    private lateinit var btnCamaraIA: Button // ✅ Declaración agregada
    private lateinit var cedula: String
    private lateinit var nombreUsuario: String

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
        btnCamaraIA = findViewById(R.id.btnCamaraIA)

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

        // Listener: Botón Cámara IA
        btnCamaraIA.setOnClickListener {
            val intent = Intent(this, Camara_IA_voluntario::class.java)
            startActivity(intent)
        }

        // Ajustar padding por las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
