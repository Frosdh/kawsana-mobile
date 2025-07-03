package com.example.app_practicas_m5a.data.model

data class InfoVoluntario(
    val nombreCompleto: String,
    val puntosTotales: Int,
    val insignias: List<String>,
    val proximasActividades: List<String>,
    val noticias: List<String>,
    val avancePorcentaje: Double
)
