package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.ProyectoAdapterAdmin
import com.example.app_practicas_m5a.data.dao.LiderDao
import com.example.app_practicas_m5a.data.dao.ProyectoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProyectosDeLiderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProyectoAdapterAdmin
    private lateinit var tvTitulo: TextView

    private var cedulaLider: String? = null
    private var nombreLider: String = "Líder" // Valor por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_proyectos_de_lider)

        tvTitulo = findViewById(R.id.tvTituloProyectosLider)
        recyclerView = findViewById(R.id.rvProyectosLider)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProyectoAdapterAdmin(emptyList())
        recyclerView.adapter = adapter

        cedulaLider = intent.getStringExtra("cedula_lider")

        cedulaLider?.let {
            cargarDatosYLuegoProyectos(it)
        }
    }

    // En la Activity ProyectosDeLiderActivity

    private fun cargarDatosYLuegoProyectos(cedula: String) {
        lifecycleScope.launch {
            // Obtener datos del líder en background
            val lider = withContext(Dispatchers.IO) {
                LiderDao.obtenerLiderPorCedula(cedula)
            }
            lider?.let {
                nombreLider = "${it.nombres} ${it.apellidos}"
            }

            // Obtener proyectos del líder
            val proyectos = withContext(Dispatchers.IO) {
                ProyectoDao.obtenerProyectosDelLider(cedula) // debe devolver List<Proyectos>
            }

            // Actualizar UI en main thread
            if (proyectos.isNotEmpty()) {
             // proyectos es List<Proyectos>, coincide con adapter
                tvTitulo.text = "Proyectos de $nombreLider"
            } else {
                tvTitulo.text = "No hay proyectos para $nombreLider"
            }
        }
    }
}
