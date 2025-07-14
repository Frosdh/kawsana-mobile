package com.example.app_practicas_m5a.data.model

data class Usuario(
    val id: Long,
    val cedula: String,
    val contraseña: String,
    val tipo_usuario: String,
    val nombres: String,
    // Otros campos que uses
)