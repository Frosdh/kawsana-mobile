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
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pagina_principal_adm : AppCompatActivity() {

    private lateinit var tvTituloAdmin: TextView
    private lateinit var tvNombreAdmin: TextView
    private lateinit var tvCorreoAdmin: TextView
    private lateinit var imgPerfil: ImageView
    private lateinit var btnVerPerfil: Button

    private lateinit var cedula: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_adm)


        // Vincular UI
        tvTituloAdmin = findViewById(R.id.tvTituloAdmin)
        tvNombreAdmin = findViewById(R.id.tvNombreAdmin)
        tvCorreoAdmin = findViewById(R.id.tvCorreoAdmin)
        imgPerfil = findViewById(R.id.imgPerfil)
        btnVerPerfil = findViewById(R.id.btnVerPerfil)

        // Obtener cédula enviada desde Login
        cedula = intent.getStringExtra("cedula") ?: ""

        // Buscar en base de datos con DAO
        lifecycleScope.launch {
            val admin = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorCedula(cedula)
            }

            if (admin != null) {
                tvNombreAdmin.text = "Nombre: ${admin.nombres} ${admin.apellidos}"
                tvCorreoAdmin.text = "Correo: ${admin.email}"
            } else {
                Toast.makeText(this@Pagina_principal_adm, "No se encontró el administrador", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción del botón
        btnVerPerfil.setOnClickListener {
            val intent = Intent(this, Perfil_Admin::class.java)
            intent.putExtra("cedula", cedula)
            startActivity(intent)
            setContentView(R.layout.activity_perfil_admin)

        }

        // Padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInicioAdmin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
