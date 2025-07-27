package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel

class CiudadanoAdapter(
    private var ciudadanos: List<CoreUsuarioModel>
) : RecyclerView.Adapter<CiudadanoAdapter.CiudadanoViewHolder>() {

    inner class CiudadanoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreCiudadano)
        val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreoCiudadano)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefonoCiudadano)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CiudadanoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ciudadano, parent, false)
        return CiudadanoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CiudadanoViewHolder, position: Int) {
        val ciudadano = ciudadanos[position]
        holder.tvNombre.text = "${ciudadano.nombres} ${ciudadano.apellidos}"
        holder.tvCorreo.text = ciudadano.email ?: "Sin correo"
        holder.tvTelefono.text = ciudadano.telefono ?: "Sin teléfono"
    }

    override fun getItemCount(): Int = ciudadanos.size

    fun updateList(nuevosCiudadanos: List<CoreUsuarioModel>) {
        ciudadanos = nuevosCiudadanos
        notifyDataSetChanged()
    }
}
