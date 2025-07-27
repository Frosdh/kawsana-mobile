package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.ProyectoModel
import com.example.app_practicas_m5a.data.model.core_proyecto
import java.sql.Connection
import java.sql.ResultSet

object ProyectoDao {

    fun obtenerProyectosDelLider(cedulaLider: String): List<core_proyecto> {
        val listaProyectos = mutableListOf<core_proyecto>()
        val conn: Connection = MySqlConexion.getConexion() ?: return listaProyectos

        val sql = """
            SELECT p.id, p.nombre, p.descripcion, p.fecha_inicio, p.fecha_fin, p.estado,
                   p.organizacion_id, IFNULL(p.progreso, '0') AS progreso
            FROM core_proyecto p
            JOIN core_liderproyectobarrio lp ON p.id = lp.proyecto_id
            JOIN core_usuario u ON lp.usuario_id = u.id
            WHERE u.cedula = ? AND p.estado = 1
        """.trimIndent()

        var stmt = conn.prepareStatement(sql)
        var rs: ResultSet? = null

        try {
            stmt.setString(1, cedulaLider)
            rs = stmt.executeQuery()

            while (rs.next()) {
                val proyecto = core_proyecto(
                    id = rs.getLong("id"),
                    nombre = rs.getString("nombre"),
                    descripcion = rs.getString("descripcion"),
                    fecha_inicio = rs.getString("fecha_inicio"),
                    fecha_fin = rs.getString("fecha_fin"),
                    estado = rs.getBoolean("estado"),
                    organizacion_id = rs.getLong("organizacion_id"),
                    progreso = rs.getString("progreso")
                )
                listaProyectos.add(proyecto)
            }
        } catch (ex: Exception) {
            Log.e("ProyectoDao", "Error al obtener proyectos del líder: ${ex.message}", ex)
        } finally {
            rs?.close()
            stmt.close()
            conn.close()
        }

        return listaProyectos
    }

}
