package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.ActividadResumen
import com.example.app_practicas_m5a.data.model.BarrioProgreso
import com.example.app_practicas_m5a.data.model.BarrioUsuariosLideres
import java.sql.Connection

object BarrioDaoProgresoGrafico {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    // Método que obtiene el top 6 barrios con mayor progreso numérico
    fun obtenerTop6Barrios(): List<BarrioProgreso> {
        val lista = mutableListOf<BarrioProgreso>()
        val conn = getConnection() ?: return lista

        val sql = """
            SELECT b.nombre AS barrio, pb.progreso AS progreso
            FROM core_progresobarrio pb
            JOIN core_barrio b ON pb.barrio_id = b.id
            ORDER BY pb.progreso DESC
            LIMIT 6
        """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val barrio = rs.getString("barrio")
                            val progreso = rs.getDouble("progreso").toFloat()
                            lista.add(BarrioProgreso(barrio, progreso))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BarrioDaoProgresoGrafico", "Error al obtener top barrios", e)
        }

        return lista
    }

    // Método: obtener todos los barrios ordenados por promedio de progreso numérico
    fun obtenerBarriosOrdenadosPorProgreso(): List<BarrioProgreso> {
        val lista = mutableListOf<BarrioProgreso>()
        val conn = getConnection() ?: return lista

        val sql = """
            SELECT b.nombre AS barrio, AVG(pb.progreso) AS progreso_promedio
            FROM core_progresobarrio pb
            JOIN core_barrio b ON pb.barrio_id = b.id
            GROUP BY b.nombre
            ORDER BY progreso_promedio DESC
        """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val barrio = rs.getString("barrio")
                            val progreso = rs.getDouble("progreso_promedio").toFloat()
                            lista.add(BarrioProgreso(barrio, progreso))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BarrioDaoProgresoGrafico", "Error al obtener barrios ordenados por progreso", e)
        }

        return lista
    }

    // Método: obtener usuarios y líderes por barrio para porcentaje
    fun obtenerUsuariosYLideresPorBarrio(): List<BarrioUsuariosLideres> {
        val lista = mutableListOf<BarrioUsuariosLideres>()
        val conn = getConnection() ?: return lista

        val sql = """
            SELECT 
              b.nombre AS barrio,
              COUNT(DISTINCT u.id) AS total_usuarios,
              COUNT(DISTINCT l.usuario_id) AS total_lideres
            FROM core_barrio b
            LEFT JOIN core_usuario u ON u.barrio_id = b.id AND u.estado = 1
            LEFT JOIN core_liderproyectobarrio l ON l.barrio_id = b.id
            GROUP BY b.id, b.nombre
            ORDER BY b.nombre;
        """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val barrio = rs.getString("barrio")
                            val totalUsuarios = rs.getInt("total_usuarios")
                            val totalLideres = rs.getInt("total_lideres")
                            lista.add(BarrioUsuariosLideres(barrio, totalUsuarios, totalLideres))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BarrioDaoProgresoGrafico", "Error al obtener usuarios y líderes por barrio", e)
        }

        return lista
    }
    // En BarrioDaoProgresoGrafico.kt

    fun obtenerResumenActividades(): ActividadResumen {
        val conn = getConnection() ?: return ActividadResumen(0, 0)
        var realizadas = 0
        var noRealizadas = 0

        val sql = """
        SELECT 
            SUM(CASE WHEN estado = 1 THEN 1 ELSE 0 END) AS realizadas,
            SUM(CASE WHEN estado = 0 THEN 1 ELSE 0 END) AS no_realizadas
        FROM core_actividad
    """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        if (rs.next()) {
                            realizadas = rs.getInt("realizadas")
                            noRealizadas = rs.getInt("no_realizadas")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("BarrioDaoProgresoGrafico", "Error al obtener resumen de actividades", e)
        }

        return ActividadResumen(realizadas, noRealizadas)
    }

}
