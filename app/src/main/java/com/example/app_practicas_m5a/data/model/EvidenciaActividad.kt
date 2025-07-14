package com.example.app_practicas_m5a.data.model

data class EvidenciaActividad(
    val archivoUrl: String,
    val tipoArchivo: String,
    val descripcion: String,
    val fechaSubida: String,       // Formato: "yyyy-MM-dd"
    val actividadId: Long,
    val usuarioId: Long,
    val esValida: Boolean,
    val fechaValidacion: String?,  // Puede ser null, formato "yyyy-MM-dd"
    val validadorId: Long?         // Puede ser null
)
