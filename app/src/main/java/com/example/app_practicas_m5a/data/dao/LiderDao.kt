package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import java.sql.Connection
import java.sql.ResultSet

object LiderDao {

    fun obtenerTodosLosLideres(): List<CoreUsuarioModel> {
        val listaLideres = mutableListOf<CoreUsuarioModel>()
        val conn: Connection = MySqlConexion.getConexion() ?: return listaLideres

        val sql = """
            SELECT id, email, contraseña, tipo_usuario, fecha_registro, estado,
                   nombres, apellidos, cedula, telefono, direccion, fecha_nacimiento,
                   barrio_id, usuario, puntos
            FROM core_usuario
            WHERE tipo_usuario = 'lider'
        """.trimIndent()

        try {
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val lider = mapResultSetToUsuario(rs)
                        listaLideres.add(lider)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("LiderDao", "Error al obtener líderes: ${e.message}")
            e.printStackTrace()
        } finally {
            conn.close()
        }
        return listaLideres
    }

    private fun mapResultSetToUsuario(rs: ResultSet): CoreUsuarioModel {
        return CoreUsuarioModel(
            id = rs.getLong("id"),
            email = rs.getString("email"),
            contraseña = rs.getString("contraseña"),
            tipo_usuario = rs.getString("tipo_usuario"),
            fecha_registro = rs.getDate("fecha_registro"),
            estado = rs.getBoolean("estado"),
            nombres = rs.getString("nombres"),
            apellidos = rs.getString("apellidos"),
            cedula = rs.getString("cedula"),
            telefono = rs.getString("telefono"),
            direccion = rs.getString("direccion"),
            fecha_nacimiento = rs.getDate("fecha_nacimiento"),
            barrio_id = rs.getLong("barrio_id"),
            usuario = rs.getString("usuario"),
            puntos = rs.getInt("puntos")
        )
    }
    fun obtenerLiderPorCedula(cedula: String): CoreUsuarioModel? {
        val conn = MySqlConexion.getConexion() ?: return null
        var lider: CoreUsuarioModel? = null
        try {
            val sql = "SELECT * FROM core_usuario WHERE cedula = ? AND tipo_usuario = 'lider'"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, cedula)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    lider = CoreUsuarioModel(
                        id = rs.getLong("id"),
                        email = rs.getString("email"),
                        contraseña = rs.getString("contraseña"),
                        tipo_usuario = rs.getString("tipo_usuario"),
                        fecha_registro = rs.getDate("fecha_registro"),
                        estado = rs.getBoolean("estado"),
                        nombres = rs.getString("nombres"),
                        apellidos = rs.getString("apellidos"),
                        cedula = rs.getString("cedula"),
                        telefono = rs.getString("telefono"),
                        direccion = rs.getString("direccion"),
                        fecha_nacimiento = rs.getDate("fecha_nacimiento"),
                        barrio_id = rs.getLong("barrio_id"),
                        usuario = rs.getString("usuario"),
                        puntos = rs.getInt("puntos")
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
        return lider
    }
}
