package com.example.app_practicas_m5a.data.dao

import java.sql.Connection

object InfoAdminDao {

    fun obtenerInfoPorCedula(cedula: String): Pair<String, String>? {
        val conn = MySqlConexion.getConexion() ?: return null
        try {
            conn.prepareStatement("SELECT nombres, apellidos, correo FROM core_usuario WHERE cedula = ?").use { stmt ->
                stmt.setString(1, cedula)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        val nombre = rs.getString("nombres")
                        val apellido = rs.getString("apellidos")
                        val correo = rs.getString("correo")
                        val nombreCompleto = "$nombre $apellido"
                        return Pair(nombreCompleto, correo)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
        return null
    }
}
