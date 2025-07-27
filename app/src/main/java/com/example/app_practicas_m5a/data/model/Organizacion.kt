package com.example.app_practicas_m5a.data.model

data class Organizacion(
    val id: Long,
    val nombre: String,
    val direccion: String,
    val telefono_contacto: String,
    val email_contacto: String,  // Nuevo campo
    val representante: String,
    val ruc: String              // Nuevo campo
)
