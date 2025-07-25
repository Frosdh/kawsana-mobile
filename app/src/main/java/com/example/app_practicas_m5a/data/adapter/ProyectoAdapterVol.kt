package com.example.app_practicas_m5a.data.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.ProyectoModel
import com.example.app_practicas_m5a.presentacion.ui.ActividadesDeProyectoActivity
import java.text.SimpleDateFormat
import java.util.*

class ProyectoAdapterVol(
    private val proyectos: List<ProyectoModel>,
    private val onClick: (ProyectoModel) -> Unit
) : RecyclerView.Adapter<ProyectoAdapterVol.ProyectoViewHolder>() {

    inner class ProyectoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProyecto)
        val tvFechas: TextView = itemView.findViewById(R.id.tvFechasProyecto)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionProyecto)
        val btnVerDetalles: Button = itemView.findViewById(R.id.btnVerDetalles) // <- agregar

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proyecto, parent, false)
        return ProyectoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        val proyecto = proyectos[position]
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val contexto = holder.itemView.context   // <--- aquí el contexto

        val fechaInicio = formato.format(proyecto.fechaInicio)
        val fechaFin = proyecto.fechaFin?.let { formato.format(it) } ?: "No especificada"

        holder.tvNombre.text = proyecto.nombre
        holder.tvFechas.text = "Desde: $fechaInicio - Hasta: $fechaFin"
        holder.tvDescripcion.text = proyecto.descripcion.ifBlank { "Sin descripción" }

        // Click en el botón "Ver detalles"
        holder.btnVerDetalles.setOnClickListener {
            val intent = Intent(contexto, ActividadesDeProyectoActivity::class.java)
            intent.putExtra("proyecto_id", proyecto.id)
            intent.putExtra("proyecto_nombre", proyecto.nombre)
            contexto.startActivity(intent)
        }

    }



    override fun getItemCount(): Int = proyectos.size
}
