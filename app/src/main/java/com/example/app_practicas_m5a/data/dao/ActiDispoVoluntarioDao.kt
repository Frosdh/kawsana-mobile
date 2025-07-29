package com.example.app_practicas_m5a.data.dao

import android.util.Log
import core_actividad
import java.sql.Connection

object ActiDispoVoluntarioDao {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    fun obtenerActividadesActivas(): List<core_actividad> {
        val actividades = mutableListOf<core_actividad>()
        val conn = getConnection() ?: run {
            Log.e("ActiDispoVoluntarioDao", "No se pudo obtener conexión a la base de datos")
            return actividades
        }

        val sql = """
            SELECT id, nombre, fecha_inicio, fecha_fin, descripcion, estado, puntos, proyecto_id
            FROM core_actividad
            WHERE estado = 1
            ORDER BY fecha_inicio ASC
        """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val actividad = core_actividad(
                                id = rs.getLong("id"),
                                nombre = rs.getString("nombre"),
                                fecha_inicio = rs.getDate("fecha_inicio")?.toString() ?: "",
                                fecha_fin = rs.getDate("fecha_fin")?.toString(),
                                descripcion = rs.getString("descripcion"),
                                estado = rs.getBoolean("estado"),
                                puntos = rs.getInt("puntos"),
                                proyecto_id = rs.getLong("proyecto_id")
                            )
                            actividades.add(actividad)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("ActiDispoVoluntarioDao", "Error al obtener actividades: ", ex)
        }

        return actividades
    }

    fun obtenerActividadesPorProyecto(proyectoId: Long): List<core_actividad> {
        val actividades = mutableListOf<core_actividad>()
        val conn = getConnection() ?: run {
            Log.e("ActiDispoVoluntarioDao", "No se pudo obtener conexión a la base de datos")
            return actividades
        }

        val sql = """
        SELECT id, nombre, fecha_inicio, fecha_fin, descripcion, estado, puntos, proyecto_id
        FROM core_actividad
        WHERE estado = 1 AND proyecto_id = ?
        ORDER BY fecha_inicio ASC
    """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setLong(1, proyectoId)
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val actividad = core_actividad(
                                id = rs.getLong("id"),
                                nombre = rs.getString("nombre"),
                                fecha_inicio = rs.getDate("fecha_inicio")?.toString() ?: "",
                                fecha_fin = rs.getDate("fecha_fin")?.toString(),
                                descripcion = rs.getString("descripcion"),
                                estado = rs.getBoolean("estado"),
                                puntos = rs.getInt("puntos"),
                                proyecto_id = rs.getLong("proyecto_id")
                            )
                            actividades.add(actividad)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("ActiDispoVoluntarioDao", "Error al obtener actividades del proyecto: ", ex)
        }

        return actividades
    }
    // ✅ ACTIVIDADES SIN EVIDENCIA O RECHAZADAS (usuario no ha subido nada)
    fun obtenerActividadesSinEvidenciaPorProyecto(proyectoId: Long): List<core_actividad> {
        val lista = mutableListOf<core_actividad>()
        val conn = getConnection() ?: return lista

        val sql = """
            SELECT DISTINCT a.* FROM core_actividad a
            LEFT JOIN core_evidenciaactividad e ON a.id = e.actividad_id
            WHERE a.proyecto_id = ? AND a.estado = 1
              AND (e.id IS NULL OR e.estado = 'rechazado')
        """.trimIndent()

        try {
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, proyectoId)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        lista.add(
                            core_actividad(
                                id = rs.getLong("id"),
                                nombre = rs.getString("nombre"),
                                fecha_inicio = rs.getDate("fecha_inicio")?.toString() ?: "",
                                fecha_fin = rs.getDate("fecha_fin")?.toString(),
                                descripcion = rs.getString("descripcion"),
                                estado = rs.getBoolean("estado"),
                                puntos = rs.getInt("puntos"),
                                proyecto_id = rs.getLong("proyecto_id")
                            )
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("DAO", "Error obtenerActividadesSinEvidenciaPorProyecto", ex)
        } finally {
            conn.close()
        }

        return lista
    }

    // ✅ ACTIVIDADES CON EVIDENCIA PENDIENTE
    fun obtenerActividadesConEvidenciaPendientePorProyecto(proyectoId: Long): List<core_actividad> {
        val lista = mutableListOf<core_actividad>()
        val conn = getConnection() ?: return lista

        val sql = """
            SELECT DISTINCT a.* FROM core_actividad a
            INNER JOIN core_evidenciaactividad e ON a.id = e.actividad_id
            WHERE a.proyecto_id = ? AND a.estado = 1
              AND e.estado = 'en_espera'
        """.trimIndent()

        try {
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, proyectoId)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        lista.add(
                            core_actividad(
                                id = rs.getLong("id"),
                                nombre = rs.getString("nombre"),
                                fecha_inicio = rs.getDate("fecha_inicio")?.toString() ?: "",
                                fecha_fin = rs.getDate("fecha_fin")?.toString(),
                                descripcion = rs.getString("descripcion"),
                                estado = rs.getBoolean("estado"),
                                puntos = rs.getInt("puntos"),
                                proyecto_id = rs.getLong("proyecto_id")
                            )
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("DAO", "Error obtenerActividadesConEvidenciaPendientePorProyecto", ex)
        } finally {
            conn.close()
        }

        return lista
    }

    // ✅ ACTIVIDADES CON EVIDENCIA APROBADA
    fun obtenerActividadesAprobadasPorProyecto(proyectoId: Long): List<core_actividad> {
        val lista = mutableListOf<core_actividad>()
        val conn = getConnection() ?: return lista

        val sql = """
            SELECT DISTINCT a.* FROM core_actividad a
            INNER JOIN core_evidenciaactividad e ON a.id = e.actividad_id
            WHERE a.proyecto_id = ? AND a.estado = 1
              AND e.estado = 'aprobado'
        """.trimIndent()

        try {
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, proyectoId)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        lista.add(
                            core_actividad(
                                id = rs.getLong("id"),
                                nombre = rs.getString("nombre"),
                                fecha_inicio = rs.getDate("fecha_inicio")?.toString() ?: "",
                                fecha_fin = rs.getDate("fecha_fin")?.toString(),
                                descripcion = rs.getString("descripcion"),
                                estado = rs.getBoolean("estado"),
                                puntos = rs.getInt("puntos"),
                                proyecto_id = rs.getLong("proyecto_id")
                            )
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("DAO", "Error obtenerActividadesAprobadasPorProyecto", ex)
        } finally {
            conn.close()
        }

        return lista
    }
}
