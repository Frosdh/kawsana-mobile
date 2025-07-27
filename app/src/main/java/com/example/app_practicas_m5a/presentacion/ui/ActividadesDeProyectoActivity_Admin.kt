package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.ActividadAdapter_Admin
import com.example.app_practicas_m5a.data.dao.ActiDispoVoluntarioDao
import core_actividad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActividadesDeProyectoActivity_Admin : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActividadAdapter_Admin
    private var proyectoId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades_de_proyecto_admin)

        recyclerView = findViewById(R.id.recyclerActividadesAdmin)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ActividadAdapter_Admin(emptyList())
        recyclerView.adapter = adapter

        proyectoId = intent.getLongExtra("proyecto_id", -1L)

        if (proyectoId != -1L) {
            cargarActividades(proyectoId)
        } else {
            Toast.makeText(this, "Proyecto inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarActividades(proyectoId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val actividades = ActiDispoVoluntarioDao.obtenerActividadesPorProyecto(proyectoId)
            withContext(Dispatchers.Main) {
                if (actividades.isNotEmpty()) {
                    adapter.updateList(actividades)
                } else {
                    Toast.makeText(this@ActividadesDeProyectoActivity_Admin, "No hay actividades para este proyecto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
