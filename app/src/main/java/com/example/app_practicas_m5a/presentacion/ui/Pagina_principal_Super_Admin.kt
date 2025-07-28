package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_Super_Admin : AppCompatActivity() {

    private lateinit var tvNombreSuperAdmin: TextView
    private lateinit var tvCorreoSuperAdmin: TextView
    private lateinit var imgPerfilSuper: ImageView
    private lateinit var btnVerPerfilSuper: Button

    // Cards
    private lateinit var cardOrganizaciones: LinearLayout
    private lateinit var cardLideres: LinearLayout
    private lateinit var cardActividades: LinearLayout
    private lateinit var cardGraficas: LinearLayout

    private lateinit var usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_super_admin)

        // Referencias
        tvNombreSuperAdmin = findViewById(R.id.tvNombreSuperAdmin)
        tvCorreoSuperAdmin = findViewById(R.id.tvCorreoSuperAdmin)
        imgPerfilSuper = findViewById(R.id.imgPerfilSuper)
        btnVerPerfilSuper = findViewById(R.id.btnVerPerfilSuper)

        // Cards
        cardOrganizaciones = findViewById(R.id.cardOrganizaciones)
        cardLideres = findViewById(R.id.cardLideres)
        cardActividades = findViewById(R.id.cardActividades)
        cardGraficas = findViewById(R.id.cardGraficas)

        usuario = intent.getStringExtra("usuario") ?: ""

        // Cargar datos del super admin
        lifecycleScope.launch {
            val admin = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorUsuario(usuario)
            }

            if (admin != null) {
                tvNombreSuperAdmin.text = "Nombre: ${admin.nombres} ${admin.apellidos}"
                tvCorreoSuperAdmin.text = "Correo: ${admin.email}"
            }
        }

        val btnPerfilSuperAdmin: Button = findViewById(R.id.btnVerPerfilSuper)

        btnPerfilSuperAdmin.setOnClickListener {
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
