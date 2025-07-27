package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel

class LiderAdapter(
    private var lideres: List<CoreUsuarioModel>,
    private val onLiderClick: (CoreUsuarioModel) -> Unit
) : RecyclerView.Adapter<LiderAdapter.LiderViewHolder>() {

    inner class LiderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreLider)
        val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreoLider)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefonoLider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lider, parent, false)
        return LiderViewHolder(view)
    }

    override fun onBindViewHolder(holder: LiderViewHolder, position: Int) {
        val lider = lideres[position]
        holder.tvNombre.text = "${lider.nombres} ${lider.apellidos}"
        holder.tvCorreo.text = lider.email.ifEmpty { "Sin correo" }
        holder.tvTelefono.text = lider.telefono.ifEmpty { "Sin teléfono" }

        holder.itemView.setOnClickListener {
            onLiderClick(lider)
        }
    }

    override fun getItemCount(): Int = lideres.size

    fun updateList(nuevosLideres: List<CoreUsuarioModel>) {
        lideres = nuevosLideres
        notifyDataSetChanged()
    }
}
