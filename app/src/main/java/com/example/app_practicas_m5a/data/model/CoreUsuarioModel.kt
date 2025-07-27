package com.example.app_practicas_m5a.data.model

import java.util.*

import java.util.Date

data class CoreUsuarioModel(
    var id: Long = 0,
    var email: String,
    var contraseña: String,
    var tipo_usuario: String,
    var fecha_registro: Date? = null,   // debe ser nullable
    var estado: Boolean,
    var nombres: String,
    var apellidos: String,
    var cedula: String,
    var telefono: String,
    var direccion: String,
    var fecha_nacimiento: Date? = null, // debe ser nullable
    var barrio_id: Long? = null,
    var usuario: String,
    var puntos: Int = 0

)
