package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.ActividadAdapter_Admin
import com.example.app_practicas_m5a.data.dao.ActiDispoVoluntarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActividadesDeProyectoActivity_Admin : AppCompatActivity() {

    private var proyectoId: Long = -1L

    private lateinit var rvSinSubir: RecyclerView
    private lateinit var rvEnRevision: RecyclerView
    private lateinit var rvAprobadas: RecyclerView

    private lateinit var layoutSinSubir: LinearLayout
    private lateinit var layoutEnRevision: LinearLayout
    private lateinit var layoutAprobadas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades_de_proyecto_admin)

        // Inicialización layouts con IDs corregidos
        layoutSinSubir = findViewById(R.id.layoutPendientes)
        layoutEnRevision = findViewById(R.id.layoutEnRevision)
        layoutAprobadas = findViewById(R.id.layoutAprobadas)

        // Inicialización RecyclerViews con IDs corregidos
        rvSinSubir = findViewById(R.id.recycler_sin_subir)
        rvEnRevision = findViewById(R.id.recycler_en_revision)
        rvAprobadas = findViewById(R.id.recycler_aprobadas)

        // Configurar LayoutManagers
        rvSinSubir.layoutManager = LinearLayoutManager(this)
        rvEnRevision.layoutManager = LinearLayoutManager(this)
        rvAprobadas.layoutManager = LinearLayoutManager(this)

        // Obtener ID del proyecto desde intent
        proyectoId = intent.getLongExtra("proyecto_id", -1L)
        if (proyectoId != -1L) {
            cargarActividadesSegmentadas(proyectoId)
        } else {
            Toast.makeText(this, "Proyecto inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarActividadesSegmentadas(proyectoId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val sinSubir = ActiDispoVoluntarioDao.obtenerActividadesSinEvidenciaPorProyecto(proyectoId)
            val enRevision = ActiDispoVoluntarioDao.obtenerActividadesConEvidenciaPendientePorProyecto(proyectoId)
            val aprobadas = ActiDispoVoluntarioDao.obtenerActividadesAprobadasPorProyecto(proyectoId)

            withContext(Dispatchers.Main) {
                if (sinSubir.isNotEmpty()) {
                    rvSinSubir.adapter = ActividadAdapter_Admin(sinSubir)
                    layoutSinSubir.visibility = View.VISIBLE
                } else {
                    layoutSinSubir.visibility = View.GONE
                }

                if (enRevision.isNotEmpty()) {
                    rvEnRevision.adapter = ActividadAdapter_Admin(enRevision)
                    layoutEnRevision.visibility = View.VISIBLE
                } else {
                    layoutEnRevision.visibility = View.GONE
                }

                if (aprobadas.isNotEmpty()) {
                    rvAprobadas.adapter = ActividadAdapter_Admin(aprobadas)
                    layoutAprobadas.visibility = View.VISIBLE
                } else {
                    layoutAprobadas.visibility = View.GONE
                }
            }
        }
    }
}
