package com.example.app_practicas_m5a.data.PerfilCompletoModel

data class PerfilCompletoModel(
    val id: Long,
    val nombres: String,
    val usuario: String,
    val apellidos: String,
    val cedula: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val tipo_usuario: String,
    val nombreBarrio: String,
    val nombreParroquia: String,
    val nombreCiudad: String,
    val barrio_id: Long?,
    val parroquia_id: Long?,
    val ciudad_id: Long?,
    val puntos: Int
)
