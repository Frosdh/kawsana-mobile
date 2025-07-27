package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.ActividadModel
import java.sql.ResultSet

object ActividadDao {

    fun obtenerActividadesDeLider(cedulaLider: String): List<ActividadModel> {
        val listaActividades = mutableListOf<ActividadModel>()
        val conn = MySqlConexion.getConexion() ?: return listaActividades

        val sql = """
            SELECT a.id, a.nombre, a.fecha_inicio, a.fecha_fin, a.descripcion, a.estado, a.puntos, a.proyecto_id
            FROM core_actividad a
            JOIN core_proyecto p ON a.proyecto_id = p.id
            JOIN core_liderproyectobarrio lp ON p.id = lp.proyecto_id
            JOIN core_usuario u ON lp.usuario_id = u.id
            WHERE u.cedula = ? AND a.estado = 1
            ORDER BY a.fecha_inicio DESC
        """.trimIndent()

        var stmt = conn.prepareStatement(sql)
        var rs: ResultSet? = null

        try {
            stmt.setString(1, cedulaLider)
            rs = stmt.executeQuery()

            while (rs.next()) {
                val actividad = ActividadModel(
                    id = rs.getLong("id"),
                    nombre = rs.getString("nombre"),
                    fechaInicio = rs.getDate("fecha_inicio"),
                    fechaFin = rs.getDate("fecha_fin"),
                    descripcion = rs.getString("descripcion"),
                    estado = rs.getBoolean("estado"),
                    puntos = rs.getInt("puntos"),
                    proyectoId = rs.getLong("proyecto_id")
                )
                listaActividades.add(actividad)
            }
        } catch (ex: Exception) {
            Log.e("ActividadDao", "Error en obtenerActividadesDeLider: ${ex.message}")
            ex.printStackTrace()
        } finally {
            rs?.close()
            stmt.close()
            conn.close()
        }

        return listaActividades
    }
    fun obtenerActividadesSinEvidencia(usuario: String): List<ActividadModel> {
        val lista = mutableListOf<ActividadModel>()
        val conn = MySqlConexion.getConexion() ?: return lista

        val sql = """
        SELECT a.id, a.nombre, a.fecha_inicio, a.fecha_fin, a.descripcion, a.estado, a.puntos, a.proyecto_id
        FROM core_actividad a
        JOIN core_proyecto p ON a.proyecto_id = p.id
        JOIN core_liderproyectobarrio lp ON p.id = lp.proyecto_id
        JOIN core_usuario u ON lp.usuario_id = u.id
        WHERE u.usuario = ? AND a.estado = 0
        ORDER BY a.fecha_inicio DESC
    """.trimIndent()

        val stmt = conn.prepareStatement(sql)
        var rs: ResultSet? = null

        try {
            stmt.setString(1, usuario)
            rs = stmt.executeQuery()
            while (rs.next()) {
                lista.add(
                    ActividadModel(
                        id = rs.getLong("id"),
                        nombre = rs.getString("nombre"),
                        fechaInicio = rs.getDate("fecha_inicio"),
                        fechaFin = rs.getDate("fecha_fin"),
                        descripcion = rs.getString("descripcion"),
                        estado = rs.getBoolean("estado"),
                        puntos = rs.getInt("puntos"),
                        proyectoId = rs.getLong("proyecto_id")
                    )
                )
            }
        } catch (ex: Exception) {
            Log.e("ActividadDao", "Error obtenerActividadesSinEvidencia: ${ex.message}")
        } finally {
            rs?.close()
            stmt.close()
            conn.close()
        }
        return lista
    }

    fun obtenerActividadesConEvidencia(usuario: String): List<ActividadModel> {
        val lista = mutableListOf<ActividadModel>()
        val conn = MySqlConexion.getConexion() ?: return lista

        val sql = """
        SELECT a.id, a.nombre, a.fecha_inicio, a.fecha_fin, a.descripcion, a.estado, a.puntos, a.proyecto_id
        FROM core_actividad a
        JOIN core_proyecto p ON a.proyecto_id = p.id
        JOIN core_liderproyectobarrio lp ON p.id = lp.proyecto_id
        JOIN core_usuario u ON lp.usuario_id = u.id
        WHERE u.usuario = ? AND a.estado = 1
        ORDER BY a.fecha_inicio DESC
    """.trimIndent()

        val stmt = conn.prepareStatement(sql)
        var rs: ResultSet? = null

        try {
            stmt.setString(1, usuario)
            rs = stmt.executeQuery()
            while (rs.next()) {
                lista.add(
                    ActividadModel(
                        id = rs.getLong("id"),
                        nombre = rs.getString("nombre"),
                        fechaInicio = rs.getDate("fecha_inicio"),
                        fechaFin = rs.getDate("fecha_fin"),
                        descripcion = rs.getString("descripcion"),
                        estado = rs.getBoolean("estado"),
                        puntos = rs.getInt("puntos"),
                        proyectoId = rs.getLong("proyecto_id")
                    )
                )
            }
        } catch (ex: Exception) {
            Log.e("ActividadDao", "Error obtenerActividadesConEvidencia: ${ex.message}")
        } finally {
            rs?.close()
            stmt.close()
            conn.close()
        }
        return lista
    }


}
