// Proyectos_Disponibles.kt
package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.ProyectoDisponobleDao
import com.example.app_practicas_m5a.data.model.Proyectos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Proyectos_Disponibles : AppCompatActivity() {

    private lateinit var listaProyectos: List<Proyectos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyectos_disponibles)

        val layoutProyectos = findViewById<LinearLayout>(R.id.layoutProyectos)

        lifecycleScope.launch {
            // Cargar los proyectos desde la base de datos
         //   listaProyectos = withContext(Dispatchers.IO) {
         //       ProyectoDisponobleDao.obtenerTodosLosProyectos()
          //  }

            for (proyecto in listaProyectos) {
                val cardView = LayoutInflater.from(this@Proyectos_Disponibles)
                    .inflate(R.layout.card_proyecto, null)

                val tvTitulo = cardView.findViewById<TextView>(R.id.tvTituloProyecto)
                val tvDescripcion = cardView.findViewById<TextView>(R.id.tvDescripcionProyecto)
                val btnVer = cardView.findViewById<Button>(R.id.btnVerProyecto)

                tvTitulo.text = proyecto.nombre
                tvDescripcion.text = proyecto.descripcion

                btnVer.setOnClickListener {
                    val intent = Intent(this@Proyectos_Disponibles, Proyectos_Disponibles::class.java)
                    intent.putExtra("titulo", proyecto.nombre)
                    intent.putExtra("descripcion", proyecto.descripcion)
                    startActivity(intent)
                }

                layoutProyectos.addView(cardView)
            }
        }
    }
}
