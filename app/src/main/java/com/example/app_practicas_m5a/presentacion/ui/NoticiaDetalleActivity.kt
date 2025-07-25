package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.app_practicas_m5a.R

class NoticiaDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticia_detalle)

        val tvTitulo = findViewById<TextView>(R.id.tvTituloDetalle)
        val tvContenido = findViewById<TextView>(R.id.tvContenidoDetalle)
        val tvFecha = findViewById<TextView>(R.id.tvFechaDetalle)
        val imgNoticia = findViewById<ImageView>(R.id.imgNoticiaDetalle)

        // Obtener datos del intent
        val titulo = intent.getStringExtra("titulo")
        val contenido = intent.getStringExtra("contenido")
        val fecha = intent.getStringExtra("fecha")
        val imagenUrl = intent.getStringExtra("imagenUrl")

        // Asignar a vistas
        tvTitulo.text = titulo
        tvContenido.text = contenido
        tvFecha.text = fecha


        Glide.with(this)
            .load(imagenUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error_image)
            .into(imgNoticia)
    }
}
