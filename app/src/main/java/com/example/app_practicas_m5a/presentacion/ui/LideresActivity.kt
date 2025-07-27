package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
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
import com.example.app_practicas_m5a.data.adapter.LiderAdapter
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.dao.LiderDao
import com.example.app_practicas_m5a.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LideresActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LiderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lideres)

        recyclerView = findViewById(R.id.rvLideres)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = LiderAdapter(emptyList()) { lider ->
            val usuario = lider.usuario
            if (usuario.isNotEmpty()) {
                val intent = Intent(this, CiudadanosDelBarrioActivity::class.java)
                intent.putExtra("usuario_lider", usuario)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Este líder no tiene usuario asignado", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.adapter = adapter

        cargarLideres()
    }

    private fun cargarLideres() {
        lifecycleScope.launch(Dispatchers.IO) {
            val lideres = CoreUsuarioDao.obtenerLideres()
            withContext(Dispatchers.Main) {
                adapter.updateList(lideres)
            }
        }
    }
}
