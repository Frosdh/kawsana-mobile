package com.example.app_practicas_m5a.data.model

import java.util.*

data class CoreUsuarioModel(
    var id: Long = 0,
    var email: String,
    var contraseña: String,
    var tipo_usuario: String,
    var fecha_registro: Date,
    var estado: Boolean,
    var nombres: String,
    var apellidos: String,
    var cedula: String,
    var telefono: String,
    var direccion: String,
    var fecha_nacimiento: Date? = null,
    val barrio_id: Long? = null
)
