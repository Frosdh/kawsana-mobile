package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
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

class Actividades_Disponibles_Vol : AppCompatActivity() {

    private lateinit var rvListaActividades: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        rvListaActividades = findViewById(R.id.rvListaActividades)
        rvListaActividades.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val actividades = withContext(Dispatchers.IO) {
                ActiDispoVoluntarioDao.obtenerActividadesActivas()
            }

            if (actividades.isNotEmpty()) {
                val adapter = ActividadAdapterVol(actividades) { actividad ->
                    Toast.makeText(this@Actividades_Disponibles_Vol, "Seleccionaste: ${actividad.nombre}", Toast.LENGTH_SHORT).show()
                }
                rvListaActividades.adapter = adapter
            } else {
                Toast.makeText(this@Actividades_Disponibles_Vol, "No hay actividades disponibles por el momento.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
