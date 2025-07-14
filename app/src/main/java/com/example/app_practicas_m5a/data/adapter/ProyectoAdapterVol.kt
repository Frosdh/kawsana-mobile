package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.core_proyecto

class ProyectoAdapterVol(
    private val proyectos: List<core_proyecto>,
    private val onClick: (core_proyecto) -> Unit
) : RecyclerView.Adapter<ProyectoAdapterVol.ProyectoViewHolder>() {

    inner class ProyectoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProyecto)
        val tvFechas: TextView = itemView.findViewById(R.id.tvFechasProyecto)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionProyecto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proyecto, parent, false)
        return ProyectoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        val proyecto = proyectos[position]
        holder.tvNombre.text = proyecto.nombre
        holder.tvFechas.text = "Desde: ${proyecto.fecha_inicio} - Hasta: ${proyecto.fecha_fin ?: "No especificada"}"
        holder.tvDescripcion.text = proyecto.descripcion ?: "Sin descripción"

        holder.itemView.setOnClickListener {
            onClick(proyecto)
        }
    }

    override fun getItemCount(): Int = proyectos.size
}