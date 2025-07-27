package com.example.app_practicas_m5a.data.model

import java.util.Date

data class ActividadModel(
    val id: Long,
    val nombre: String,
    val fechaInicio: Date?,
    val fechaFin: Date?,
    val descripcion: String?,
    val estado: Boolean,
    val puntos: Int,
    val proyectoId: Long
)
