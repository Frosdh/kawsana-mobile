package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.ProyectoModel
import java.sql.Connection
import java.util.Date

object ProyectosDispoVoluntarioDao {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    fun obtenerProyectosDelVoluntario(cedulaVoluntario: String): List<ProyectoModel> {
        val listaProyectos = mutableListOf<ProyectoModel>()
        val conn = getConnection() ?: return listaProyectos

        val sql = """
            SELECT p.id, p.nombre, p.descripcion, p.fecha_inicio, p.fecha_fin, p.estado, p.organizacion_id, p.progreso
            FROM core_proyecto p
            JOIN core_liderproyectobarrio lp ON p.id = lp.proyecto_id
            JOIN core_usuario u ON lp.usuario_id = u.id
            WHERE u.cedula = ? AND p.estado = 1
        """.trimIndent()

        var stmt = conn.prepareStatement(sql)
        var rs: java.sql.ResultSet? = null

        try {
            stmt.setString(1, cedulaVoluntario)
            rs = stmt.executeQuery()

            while (rs.next()) {
                val proyecto = ProyectoModel(
                    id = rs.getLong("id"),
                    nombre = rs.getString("nombre"),
                    descripcion = rs.getString("descripcion"),
                    // Convertir java.sql.Date a java.util.Date
                    fechaInicio = rs.getDate("fecha_inicio")?.let { Date(it.time) } ?: Date(),
                    fechaFin = rs.getDate("fecha_fin")?.let { Date(it.time) } ?: Date(),
                    estado = rs.getBoolean("estado"),
                    organizacion_id = rs.getLong("organizacion_id"),
                    progreso = rs.getString("progreso")
                )
                listaProyectos.add(proyecto)
            }
        } catch (ex: Exception) {
            Log.e("ProyectoDao", "Error al obtener proyectos del voluntario: ${ex.message}")
            ex.printStackTrace()
        } finally {
            rs?.close()
            stmt?.close()
            conn.close()
        }

        return listaProyectos
    }

    fun obtenerPuntosTotalesPorCedula(cedula: String): Int {
        var totalPuntos = 0
        val conn = getConnection() ?: return 0

        val sql = """
            SELECT COALESCE(SUM(a.puntos), 0) AS total_puntos
            FROM core_proyecto p
            JOIN core_liderproyectobarrio lp ON lp.proyecto_id = p.id
            JOIN core_usuario u ON lp.usuario_id = u.id
            JOIN core_actividad a ON a.proyecto_id = p.id
            WHERE u.cedula = ? AND a.estado = 1
        """.trimIndent()

        try {
            conn.use { c ->
                c.prepareStatement(sql).use { ps ->
                    ps.setString(1, cedula)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) {
                            totalPuntos = rs.getInt("total_puntos")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ProyectoDao", "Error al obtener puntos totales: ${e.message}")
            e.printStackTrace()
        }

        return totalPuntos
    }
}
