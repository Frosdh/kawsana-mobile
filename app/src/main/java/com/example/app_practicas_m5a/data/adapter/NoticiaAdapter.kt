package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.core_noticia

class NoticiaAdapter(
    private val noticias: List<core_noticia>,
    private val onItemClick: (core_noticia) -> Unit
) : RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder>() {

    class NoticiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tvTitulo)
        val contenido: TextView = view.findViewById(R.id.tvContenido)
        val fecha: TextView = view.findViewById(R.id.tvFecha)
        val imagen: ImageView = view.findViewById(R.id.imgNoticia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = noticias[position]
        holder.titulo.text = noticia.titulo
        holder.contenido.text = noticia.contenido
        holder.fecha.text = noticia.actualizadosEn

        Glide.with(holder.itemView.context)
            .load(noticia.imagenUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error_image)
            .into(holder.imagen)

        holder.itemView.setOnClickListener {
            onItemClick(noticia)
        }
    }

    override fun getItemCount(): Int = noticias.size
}
