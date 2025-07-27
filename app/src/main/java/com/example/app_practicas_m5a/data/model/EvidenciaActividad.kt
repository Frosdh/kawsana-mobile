package com.example.app_practicas_m5a.data.model

data class EvidenciaActividad(
    val id: Long = 0,
    val descripcion: String,
    val fechaSubida: String,       // "yyyy-MM-dd HH:mm:ss"
    val actividadId: Long,
    val usuarioId: Long,
    val esValida: Boolean = false,
    val fechaValidacion: String? = null,
    val validadorId: Long? = null,
    val archivo: String,           // nombre o ruta del archivo
    val estado: String = "pendiente",
    val puntos: Int = 0
)
