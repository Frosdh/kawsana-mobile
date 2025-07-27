package com.example.app_practicas_m5a.data.model

import java.util.Date

data class Proyectos(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val fecha_inicio: Date?,
    val fecha_fin: Date?,
    val estado: Boolean,
    val organizacion_id: Long,
    val progreso: String?
)
