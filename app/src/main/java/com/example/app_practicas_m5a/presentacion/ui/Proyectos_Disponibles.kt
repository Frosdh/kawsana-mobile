package com.example.app_practicas_m5a.presentacion.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.ProyectoDisponobleDao
import com.example.app_practicas_m5a.data.model.Proyectos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Proyectos_Disponibles : AppCompatActivity() {

    private lateinit var layoutProyectos: LinearLayout
    private val proyectosMostrados = mutableListOf<Proyectos>() // Lista de proyectos visibles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectos_disponibles)

        layoutProyectos = findViewById(R.id.layoutProyectos)

        cargarProyectos()
    }

    private fun cargarProyectos() {
        layoutProyectos.removeAllViews()
        proyectosMostrados.clear()

        lifecycleScope.launch(Dispatchers.IO) {
            val proyectos = ProyectoDisponobleDao.obtenerTodosLosProyectos()

            withContext(Dispatchers.Main) {
                for (proyecto in proyectos) {
                    agregarProyectoAlaVista(proyecto)
                    proyectosMostrados.add(proyecto)
                }
            }
        }
    }

    private val detalleProyectoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val proyectoIdEliminado = result.data?.getLongExtra("proyecto_id", -1) ?: -1
            if (proyectoIdEliminado != -1L) {
                removerProyectoDeVista(proyectoIdEliminado)
            }
        }
    }

    private fun agregarProyectoAlaVista(proyecto: Proyectos) {
        val cardView = LayoutInflater.from(this).inflate(R.layout.card_proyecto, null)

        val tvTitulo = cardView.findViewById<TextView>(R.id.tvTituloProyecto)
        val tvDescripcion = cardView.findViewById<TextView>(R.id.tvDescripcionProyecto)
        val btnVer = cardView.findViewById<Button>(R.id.btnVerProyecto)

        tvTitulo.text = proyecto.nombre
        tvDescripcion.text = proyecto.descripcion

        lifecycleScope.launch(Dispatchers.IO) {
            val organizacion = ProyectoDisponobleDao.obtenerOrganizacionPorId(proyecto.organizacion_id)

            withContext(Dispatchers.Main) {
                btnVer.setOnClickListener {
                    val intent = Intent(this@Proyectos_Disponibles, Detalle_Proyecto::class.java)
                    intent.putExtra("proyecto_id", proyecto.id)
                    intent.putExtra("nombre", proyecto.nombre)
                    intent.putExtra("descripcion", proyecto.descripcion)
                    intent.putExtra("fecha_inicio", proyecto.fecha_inicio.toString())
                    intent.putExtra("fecha_fin", proyecto.fecha_fin.toString())

                    if (organizacion != null) {
                        intent.putExtra("nombre_org", organizacion.nombre)
                        intent.putExtra("direccion_org", organizacion.direccion)
                        intent.putExtra("telefono_org", organizacion.telefono_contacto)
                        intent.putExtra("representante_org", organizacion.representante)
                    }

                    // Enviamos el proyecto y esperamos un resultado
                    detalleProyectoLauncher.launch(intent)
                }

                layoutProyectos.addView(cardView)
                cardView.tag = proyecto.id // asignamos un tag para identificar el card
            }
        }
    }

    private fun removerProyectoDeVista(proyectoId: Long) {
        val viewToRemove = layoutProyectos.findViewWithTag<View>(proyectoId)
        layoutProyectos.removeView(viewToRemove)
    }
}
