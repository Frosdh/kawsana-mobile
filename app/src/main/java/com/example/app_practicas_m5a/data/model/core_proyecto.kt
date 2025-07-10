package com.example.app_practicas_m5a.data.model

data class core_proyecto(
    val id: Long,
    val nombre: String,
    val fecha_inicio: String,
    val fecha_fin: String?,
    val descripcion: String?,
    val estado: Boolean,
    val organizacion_id: Long
)