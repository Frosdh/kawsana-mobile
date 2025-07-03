package com.example.app_practicas_m5a.data.model

data class AuthUserModel(
    val id: Int,
    val username: String,
    val password: String,
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val is_active: Int
)