package com.example.app_practicas_m5a.data.model

import java.util.Date

data class ProyectoModel(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: Date,
    val fechaFin: Date,
    val estado: Int,
    val progreso: String
)
