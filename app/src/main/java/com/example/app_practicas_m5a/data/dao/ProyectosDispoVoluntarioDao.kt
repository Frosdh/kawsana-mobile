package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.core_proyecto
import java.sql.Connection

object ProyectosDispoVoluntarioDao {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    fun obtenerProyectosActivos(): List<core_proyecto> {
        val proyectos = mutableListOf<core_proyecto>()
        val conn = getConnection() ?: return proyectos

        val sql = """
            SELECT id, nombre, fecha_inicio, fecha_fin, descripcion, estado, organizacion_id
            FROM core_proyecto
            WHERE estado = 1
            ORDER BY fecha_inicio ASC
        """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            proyectos.add(
                                core_proyecto(
                                    id = rs.getLong("id"),
                                    nombre = rs.getString("nombre"),
                                    fecha_inicio = rs.getDate("fecha_inicio")?.toString() ?: "",
                                    fecha_fin = rs.getDate("fecha_fin")?.toString(),
                                    descripcion = rs.getString("descripcion"),
                                    estado = rs.getBoolean("estado"),
                                    organizacion_id = rs.getLong("organizacion_id")
                                )
                            )
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("ProyectoDao", "Error al obtener proyectos: ", ex)
        }

        return proyectos
    }
}