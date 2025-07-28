package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.BarrioProgreso
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
            Log.e("BarrioDao", "Error al obtener top barrios", e)
        }

        return lista
    }

    // Nuevo método: obtener todos los barrios ordenados por promedio de progreso numérico
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
            Log.e("BarrioDao", "Error al obtener barrios ordenados por progreso", e)
        }

        return lista
    }
}
