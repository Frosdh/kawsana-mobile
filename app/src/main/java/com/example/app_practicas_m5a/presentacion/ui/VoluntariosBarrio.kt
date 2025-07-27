package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.util.Log
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
    private var usuarioLider: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voluntarios_barrio)

        recyclerView = findViewById(R.id.rvVoluntarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuarioLider = intent.getStringExtra("usuario")
        Log.d("VoluntariosBarrio", "Usuario líder recibido: $usuarioLider")

        Toast.makeText(this, "Usuario recibido: $usuarioLider", Toast.LENGTH_LONG).show()

        if (usuarioLider == null) {
            Toast.makeText(this, "No se recibió el usuario del líder", Toast.LENGTH_SHORT).show()
            Log.e("VoluntariosBarrio", "Error: usuarioLider es null")
        } else {
            cargarVoluntariosDelBarrio()
        }
    }

    private fun cargarVoluntariosDelBarrio() {
        Log.d("VoluntariosBarrio", "Iniciando carga de voluntarios...")
        lifecycleScope.launch {
            listaVoluntarios = withContext(Dispatchers.IO) {
                Log.d("VoluntariosBarrio", "Consultando voluntarios en DAO...")
                CoreUsuarioDao.obtenerVoluntariosPorUsuarioLider(usuarioLider!!).also {
                    Log.d("VoluntariosBarrio", "Voluntarios encontrados: ${it.size}")
                }
            }

            if (listaVoluntarios.isNotEmpty()) {
                Log.d("VoluntariosBarrio", "Cargando lista en el RecyclerView")
                adaptador = VoluntarioAdapter(listaVoluntarios)
                recyclerView.adapter = adaptador
            } else {
                Toast.makeText(this@VoluntariosBarrio, "No hay voluntarios en tu barrio", Toast.LENGTH_SHORT).show()
                Log.w("VoluntariosBarrio", "La lista de voluntarios está vacía")
            }
        }
    }
}
