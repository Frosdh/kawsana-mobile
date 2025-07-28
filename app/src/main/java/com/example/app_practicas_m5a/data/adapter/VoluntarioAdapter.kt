package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel

class VoluntarioAdapter(private val lista: List<CoreUsuarioModel>) :
    RecyclerView.Adapter<VoluntarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefono)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccion)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voluntario, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voluntario = lista[position]
        holder.tvNombre.text = "${voluntario.nombres} ${voluntario.apellidos}"
        holder.tvEmail.text = voluntario.email
        holder.tvTelefono.text = voluntario.telefono
        holder.tvDireccion.text=voluntario.direccion

    }

    override fun getItemCount(): Int = lista.size
}
