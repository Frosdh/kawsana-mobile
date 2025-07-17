package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.adapter.NoticiaAdapter
import com.example.app_practicas_m5a.data.dao.NoticiaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoticiasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoticiaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)

        recyclerView = findViewById(R.id.recyclerNoticias)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val noticias = withContext(Dispatchers.IO) {
                NoticiaDao.obtenerNoticias()
            }

            adapter = NoticiaAdapter(noticias) { noticia ->
                val intent = Intent(this@NoticiasActivity, NoticiaDetalleActivity::class.java).apply {
                    putExtra("titulo", noticia.titulo)
                    putExtra("contenido", noticia.contenido)
                    putExtra("fecha", noticia.actualizadosEn)
                    putExtra("imagenUrl", noticia.imagenUrl)
                }
                startActivity(intent)
            }

            recyclerView.adapter = adapter
        }
    }
}
