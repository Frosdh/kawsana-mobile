package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.VoluntarioAdapter
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoluntariosBarrio : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: VoluntarioAdapter
    private var listaVoluntarios: List<CoreUsuarioModel> = emptyList()
    private var cedulaLider: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voluntarios_barrio)

        recyclerView = findViewById(R.id.rvVoluntarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cedulaLider = intent.getStringExtra("cedula")

        Toast.makeText(this, "Cédula recibida: $cedulaLider", Toast.LENGTH_LONG).show()

        if (cedulaLider == null) {
            Toast.makeText(this, "No se recibió la cédula del líder", Toast.LENGTH_SHORT).show()
        } else {
            cargarVoluntariosDelBarrio()
        }
    }

    private fun cargarVoluntariosDelBarrio() {
        lifecycleScope.launch {
            listaVoluntarios = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerVoluntariosPorCedulaLider(cedulaLider!!)
            }

            if (listaVoluntarios.isNotEmpty()) {
                adaptador = VoluntarioAdapter(listaVoluntarios)
                recyclerView.adapter = adaptador
            } else {
                Toast.makeText(this@VoluntariosBarrio, "No hay voluntarios en tu barrio", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
