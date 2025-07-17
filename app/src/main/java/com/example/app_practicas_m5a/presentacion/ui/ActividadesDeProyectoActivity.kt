package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.ActiDispoVoluntarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import core_actividad

class ActividadesDeProyectoActivity : AppCompatActivity() {

    private lateinit var tvTituloProyecto: TextView
    private lateinit var rvActividades: RecyclerView
    private lateinit var adapter: ActividadAdapterVol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades_proyecto)

        tvTituloProyecto = findViewById(R.id.tvTituloProyecto)
        rvActividades = findViewById(R.id.rvActividades)
        rvActividades.layoutManager = LinearLayoutManager(this)

        val proyectoId = intent.getLongExtra("proyecto_id", -1)
        val nombreProyecto = intent.getStringExtra("proyecto_nombre") ?: "Proyecto"

        tvTituloProyecto.text = nombreProyecto

        if (proyectoId == -1L) {
            Toast.makeText(this, "ID del proyecto inválido", Toast.LENGTH_SHORT).show()
            finish()
        }

        lifecycleScope.launch {
            val actividades = withContext(Dispatchers.IO) {
                ActiDispoVoluntarioDao.obtenerActividadesPorProyecto(proyectoId)
            }

            adapter = ActividadAdapterVol(actividades) { actividad ->
                Toast.makeText(this@ActividadesDeProyectoActivity, "Seleccionaste: ${actividad.nombre}", Toast.LENGTH_SHORT).show()
            }

            rvActividades.adapter = adapter
        }
    }
}
