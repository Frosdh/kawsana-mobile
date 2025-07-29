package com.example.app_practicas_m5a.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_practicas_m5a.R

class CarruselAdapter(private val imagenes: List<Int>) :
    RecyclerView.Adapter<CarruselAdapter.CarruselViewHolder>() {

    inner class CarruselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCarrusel: ImageView = itemView.findViewById(R.id.imgCarrusel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarruselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagen, parent, false)
        return CarruselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarruselViewHolder, position: Int) {
        holder.imgCarrusel.setImageResource(imagenes[position])
    }

    override fun getItemCount(): Int = imagenes.size // 🔍 Este debe ser 6
}

