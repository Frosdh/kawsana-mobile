package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.BarrioProgreso
import java.sql.Connection

object BarrioDaoProgresoGrafico {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    fun obtenerProgresoPorBarrio(): List<BarrioProgreso> {
        val lista = mutableListOf<BarrioProgreso>()
        val conn = getConnection() ?: return lista

        val sql = """
            SELECT b.nombre AS barrio, REPLACE(p.progreso, '%', '') AS progreso_str
            FROM core_liderproyectobarrio lb
            JOIN core_barrio b ON lb.barrio_id = b.id
            JOIN core_proyecto p ON lb.proyecto_id = p.id
        """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val barrio = rs.getString("barrio")
                            val progresoStr = rs.getString("progreso_str") ?: "0"
                            val progreso = progresoStr.toFloatOrNull() ?: 0f

                            lista.add(BarrioProgreso(barrio, progreso))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BarrioDao", "Error al obtener progreso por barrio", e)
        }

        return lista
    }
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

}
