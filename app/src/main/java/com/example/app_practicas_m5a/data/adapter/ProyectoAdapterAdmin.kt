package com.example.app_practicas_m5a.data.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.ProyectoModel
import com.example.app_practicas_m5a.data.model.Proyectos
import com.example.app_practicas_m5a.presentacion.ui.ActividadesDeProyectoActivity_Admin
import java.text.SimpleDateFormat
import java.util.*
class ProyectoAdapterAdmin(
    private var proyectos: List<Proyectos>
) : RecyclerView.Adapter<ProyectoAdapterAdmin.ProyectoViewHolder>() {

    inner class ProyectoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProyecto)
        val tvFechas: TextView = itemView.findViewById(R.id.tvFechasProyecto)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionProyecto)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstadoProyecto)
        val btnVerDetalles: Button = itemView.findViewById(R.id.btnVerDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proyecto_admin, parent, false)
        return ProyectoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        val proyecto = proyectos[position]
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val contexto = holder.itemView.context

        val fechaInicio = proyecto.fecha_inicio?.let { formato.format(it) } ?: "No especificada"
        val fechaFin = proyecto.fecha_fin?.let { formato.format(it) } ?: "No especificada"

        holder.tvNombre.text = proyecto.nombre
        holder.tvFechas.text = "Desde: $fechaInicio - Hasta: $fechaFin"
        holder.tvDescripcion.text = proyecto.descripcion.ifBlank { "Sin descripción" }

        if (proyecto.estado) {
            holder.tvEstado.text = "Activo"
            holder.tvEstado.setTextColor(Color.parseColor("#388E3C")) // Verde
        } else {
            holder.tvEstado.text = "Inactivo"
            holder.tvEstado.setTextColor(Color.parseColor("#D32F2F")) // Rojo
        }

        holder.btnVerDetalles.setOnClickListener {
            val intent = Intent(contexto, ActividadesDeProyectoActivity_Admin::class.java)
            intent.putExtra("proyecto_id", proyecto.id)
            intent.putExtra("proyecto_nombre", proyecto.nombre)
            contexto.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = proyectos.size

    fun updateList(nuevosProyectos: List<Proyectos>) {
        proyectos = nuevosProyectos
        notifyDataSetChanged()
    }
}
