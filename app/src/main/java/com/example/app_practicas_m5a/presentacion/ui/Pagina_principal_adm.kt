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
    private lateinit var btnFinal: Button


    private lateinit var cedula: String
    private var usuarioId: Long = -1  // ✅ Declarar variable de clase


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
        btnFinal = findViewById(R.id.btnFinal)

        // Obtener cédula enviada desde Login
        cedula = intent.getStringExtra("cedula") ?: ""

        // Buscar en base de datos con DAO
        lifecycleScope.launch {
            val admin = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorCedula(cedula)
            }

            if (admin != null) {
                usuarioId = admin.id  // ✅ Asignar ID

                tvNombreAdmin.text = "Nombre: ${admin.nombres} ${admin.apellidos}"
                tvCorreoAdmin.text = "Correo: ${admin.email}"
            } else {
                Toast.makeText(this@Pagina_principal_adm, "No se encontró el administrador", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción del botón: Ir a Perfil_Admin
        btnVerPerfil.setOnClickListener {
            val intent = Intent(this, Perfil_Admin::class.java)
            intent.putExtra("cedula", cedula)
            startActivity(intent)
        }


        val btnVerProyecto = findViewById<Button>(R.id.btnVerProyecto)
        btnVerProyecto.setOnClickListener {
            val intent = Intent(this, Proyectos_Disponibles::class.java)
            intent.putExtra("titulo", "Proyecto Kausana")
            intent.putExtra("descripcion", "Plataforma de apoyo social para comunidades rurales.")
            startActivity(intent)
        }

        // Acción del botón Ver Actividades
        btnFinal.setOnClickListener {
            val intent = Intent(this, Proyecto_Actuales_Lider::class.java)
            intent.putExtra("cedula", cedula)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
        }

        // Padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInicioAdmin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
