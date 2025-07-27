package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.ProyectoAdapterAdmin
import com.example.app_practicas_m5a.data.dao.ProyectoDisponobleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActividadesActivityAdmin : AppCompatActivity() {

    private lateinit var recyclerActivos: RecyclerView
    private lateinit var recyclerInactivos: RecyclerView
    private lateinit var adapterActivos: ProyectoAdapterAdmin
    private lateinit var adapterInactivos: ProyectoAdapterAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades_admin)

        recyclerActivos = findViewById(R.id.recyclerActivos)
        recyclerInactivos = findViewById(R.id.recyclerInactivos)

        recyclerActivos.layoutManager = LinearLayoutManager(this)
        recyclerInactivos.layoutManager = LinearLayoutManager(this)

        adapterActivos = ProyectoAdapterAdmin(emptyList())
        adapterInactivos = ProyectoAdapterAdmin(emptyList())

        recyclerActivos.adapter = adapterActivos
        recyclerInactivos.adapter = adapterInactivos

        cargarProyectos()
    }

    private fun cargarProyectos() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val (activos, inactivos) = ProyectoDisponobleDao.obtenerProyectosAgrupadosPorEstado()
                withContext(Dispatchers.Main) {
                    adapterActivos.updateList(activos)
                    adapterInactivos.updateList(inactivos)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ActividadesActivityAdmin,
                        "Error al cargar proyectos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    }
