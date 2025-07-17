package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.ProyectoAdapterVol
import com.example.app_practicas_m5a.data.dao.ProyectosDispoVoluntarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Proyectos_Disponibles_Vol : AppCompatActivity() {

    private lateinit var rvListaProyectos: RecyclerView
    private lateinit var cedula: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectos)

        rvListaProyectos = findViewById(R.id.rvListaProyectos)
        rvListaProyectos.layoutManager = LinearLayoutManager(this)

        // Obtener la cédula enviada desde la pantalla anterior
        cedula = intent.getStringExtra("cedula") ?: ""

        lifecycleScope.launch {
            val proyectos = withContext(Dispatchers.IO) {
                // CORREGIDO: llamado al método correcto
                ProyectosDispoVoluntarioDao.obtenerProyectosDelVoluntario(cedula)
            }

            if (proyectos.isNotEmpty()) {
                val adapter = ProyectoAdapterVol(proyectos) { proyecto ->
                    Toast.makeText(
                        this@Proyectos_Disponibles_Vol,
                        "Seleccionaste: ${proyecto.nombre}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                rvListaProyectos.adapter = adapter
            } else {
                Toast.makeText(
                    this@Proyectos_Disponibles_Vol,
                    "No hay proyectos disponibles por el momento.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
