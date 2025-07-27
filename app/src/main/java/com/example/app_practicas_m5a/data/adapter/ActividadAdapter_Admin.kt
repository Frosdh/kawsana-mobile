package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import core_actividad
import java.util.*

class ActividadAdapter_Admin(
    private var actividades: List<core_actividad>
) : RecyclerView.Adapter<ActividadAdapter_Admin.ActividadViewHolder>() {

    inner class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreActividad)
        val tvFechas: TextView = itemView.findViewById(R.id.tvFechasActividad)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionActividad)
        val tvPuntos: TextView = itemView.findViewById(R.id.tvPuntosActividad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad_admin, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]

        // Manejo de fechas
        val fechaInicio = if (actividad.fecha_inicio.isNotBlank()) actividad.fecha_inicio else "No definida"
        val fechaFin = actividad.fecha_fin ?: "No definida"

        holder.tvNombre.text = actividad.nombre
        holder.tvFechas.text = "Desde: $fechaInicio - Hasta: $fechaFin"
        holder.tvDescripcion.text = if (!actividad.descripcion.isNullOrBlank()) actividad.descripcion else "Sin descripción"
        holder.tvPuntos.text = "Puntos: ${actividad.puntos}"
    }

    override fun getItemCount(): Int = actividades.size

    fun updateList(nuevasActividades: List<core_actividad>) {
        actividades = nuevasActividades
        notifyDataSetChanged()
    }
}
