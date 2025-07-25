package com.example.app_practicas_m5a.presentacion.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import core_actividad

class ActividadAdapterVol(
    private val actividades: List<core_actividad>,
    private val onClick: (core_actividad) -> Unit
) : RecyclerView.Adapter<ActividadAdapterVol.ActividadViewHolder>() {

    inner class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreActividad)
        val tvFechas: TextView = itemView.findViewById(R.id.tvFechasActividad)
        val tvPuntos: TextView = itemView.findViewById(R.id.tvPuntosActividad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]
        holder.tvNombre.text = actividad.nombre
        holder.tvFechas.text = "Desde: ${actividad.fecha_inicio} - Hasta: ${actividad.fecha_fin ?: "No especificada"}"
        holder.tvPuntos.text = "Puntos: ${actividad.puntos}"

        holder.itemView.setOnClickListener {
            onClick(actividad)
        }
    }

    override fun getItemCount(): Int = actividades.size
}
