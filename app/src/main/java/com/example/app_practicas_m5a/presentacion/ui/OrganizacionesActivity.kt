package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.OrganizacionAdapter
import com.example.app_practicas_m5a.data.dao.OrganizacionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrganizacionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrganizacionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizaciones)

        recyclerView = findViewById(R.id.recyclerOrganizaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrganizacionAdapter(emptyList())  // Inicializa vacío
        recyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val organizaciones = OrganizacionDao.obtenerTodasOrganizaciones()
            withContext(Dispatchers.Main) {
                adapter.updateList(organizaciones)
            }
        }

    }
}
