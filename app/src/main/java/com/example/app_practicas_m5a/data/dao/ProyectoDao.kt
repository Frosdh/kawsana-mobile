package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.ProyectoModel
import java.sql.Connection

object ProyectoDao {

    fun obtenerProyectosDelLider(cedulaLider: String): List<ProyectoModel> {
        val listaProyectos = mutableListOf<ProyectoModel>()
        val conn: Connection = MySqlConexion.getConexion() ?: return listaProyectos

        val sql = """
            SELECT p.id, p.nombre, p.descripcion, p.fecha_inicio, p.fecha_fin, p.estado, p.progreso
            FROM core_proyecto p
            JOIN core_liderproyectobarrio lp ON p.id = lp.proyecto_id
            JOIN core_usuario u ON lp.usuario_id = u.id
            WHERE u.cedula = ? AND p.estado = 1
        """.trimIndent()

        var stmt = conn.prepareStatement(sql)
        var rs = null as java.sql.ResultSet?

        try {
            stmt.setString(1, cedulaLider)
            rs = stmt.executeQuery()

            while (rs.next()) {
                val proyecto = ProyectoModel(
                    id = rs.getLong("id"),
                    nombre = rs.getString("nombre"),
                    descripcion = rs.getString("descripcion"),
                    fechaInicio = rs.getDate("fecha_inicio"),
                    fechaFin = rs.getDate("fecha_fin"),
                    estado = rs.getInt("estado"),
                    progreso = rs.getString("progreso")
                )
                listaProyectos.add(proyecto)
            }
        } catch (ex: Exception) {
            Log.e("ProyectoDao", "Error al obtener proyectos del líder: ${ex.message}")
            ex.printStackTrace()
        } finally {
            rs?.close()
            stmt?.close()
            conn.close()
        }

        return listaProyectos
    }
}
