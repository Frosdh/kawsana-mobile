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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voluntario, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voluntario = lista[position]
        holder.tvNombre.text = "${voluntario.nombres} ${voluntario.apellidos} | Email: ${voluntario.email} | Tel: ${voluntario.telefono}"
    }

    override fun getItemCount(): Int = lista.size
}