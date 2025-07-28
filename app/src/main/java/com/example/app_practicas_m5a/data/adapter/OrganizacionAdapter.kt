package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.Organizacion

class OrganizacionAdapter(
    private var listaOrganizaciones: List<Organizacion>
) : RecyclerView.Adapter<OrganizacionAdapter.OrganizacionViewHolder>() {

    inner class OrganizacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvRepresentante: TextView = itemView.findViewById(R.id.tvRepresentante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_organizacion, parent, false)
        return OrganizacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrganizacionViewHolder, position: Int) {
        val organizacion = listaOrganizaciones[position]
        holder.tvNombre.text = organizacion.nombre
        holder.tvDireccion.text = organizacion.direccion
        holder.tvTelefono.text = organizacion.telefono_contacto
        holder.tvEmail.text = organizacion.email_contacto
        holder.tvRepresentante.text = organizacion.representante
    }

    override fun getItemCount() = listaOrganizaciones.size

    fun updateList(nuevaLista: List<Organizacion>) {
        listaOrganizaciones = nuevaLista
        notifyDataSetChanged()
    }
}
