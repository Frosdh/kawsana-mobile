package com.example.app_practicas_m5a.data.model

import java.util.*

data class CoreUsuarioModel(
    val id: Long = 0,
    val email: String,
    val contraseña: String,
    val tipo_usuario: String,  // "ADMIN" o "VOLUNTARIO"
    val fecha_registro: Date,
    val estado: Boolean,
    val nombres: String,
    val apellidos: String,
    val cedula: String,
    val telefono: String,
    val direccion: String,
    val fecha_nacimiento: Date? = null,
    val barrio_id: Long? = null
)