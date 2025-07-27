package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.CiudadanoAdapter
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CiudadanosDelBarrioActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CiudadanoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Opcional, para edge-to-edge si quieres

        setContentView(R.layout.activity_ciudadanos_del_barrio)

        recyclerView = findViewById(R.id.rvCiudadanos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CiudadanoAdapter(emptyList())
        recyclerView.adapter = adapter

        val usuarioLider = intent.getStringExtra("usuario_lider")
        if (!usuarioLider.isNullOrEmpty()) {
            cargarCiudadanosPorLider(usuarioLider)
        } else {
            Toast.makeText(this, "No se especificó el líder", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarCiudadanosPorLider(usuarioLider: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Este método debes implementarlo en tu CoreUsuarioDao, que busca los ciudadanos
            // que están en el mismo barrio que el líder identificado por usuarioLider.
            val ciudadanos = CoreUsuarioDao.obtenerCiudadanosPorLider(usuarioLider)
            withContext(Dispatchers.Main) {
                adapter.updateList(ciudadanos)
            }
        }
    }
}
