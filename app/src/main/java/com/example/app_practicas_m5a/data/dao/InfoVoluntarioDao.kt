package com.example.app_practicas_m5a.data.dao

import com.example.app_practicas_m5a.data.model.InfoVoluntario
import java.sql.Connection

object InfoVoluntarioDao {

    fun obtenerInfoPorCedula(cedula: String): InfoVoluntario? {
        val conn = MySqlConexion.getConexion() ?: return null

        var nombreCompleto = ""
        var puntosTotales = 0
        val insignias = mutableListOf<String>()
        val actividades = mutableListOf<String>()
        val noticias = mutableListOf<String>()
        var avance = 0.0

        try {
            // 1. Obtener nombre, id y barrio del usuario
            conn.prepareStatement("SELECT id, nombres, apellidos, barrio_id FROM core_usuario WHERE cedula = ?").use { stmtUser ->
                stmtUser.setString(1, cedula)
                stmtUser.executeQuery().use { rsUser ->
                    if (!rsUser.next()) return null
                    val userId = rsUser.getLong("id")
                    val barrioId = rsUser.getLong("barrio_id")
                    nombreCompleto = "${rsUser.getString("nombres")} ${rsUser.getString("apellidos")}"

                    // 2. Calcular puntos totales
                    conn.prepareStatement("""
                        SELECT COALESCE(SUM(a.puntos), 0) AS total
                        FROM core_evidenciaactividad e
                        JOIN core_actividad a ON e.actividad_id = a.id
                        WHERE e.usuario_id = ?
                    """.trimIndent()).use { stmtPuntos ->
                        stmtPuntos.setLong(1, userId)
                        stmtPuntos.executeQuery().use { rsPuntos ->
                            if (rsPuntos.next()) {
                                puntosTotales = rsPuntos.getInt("total")
                            }
                        }
                    }

                    // 3. Insignias
                    conn.prepareStatement("""
                        SELECT i.nombre FROM core_usuarioinsignia ui
                        JOIN core_insignia i ON ui.insignia_id = i.id
                        WHERE ui.usuario_id = ?
                    """.trimIndent()).use { stmtInsignias ->
                        stmtInsignias.setLong(1, userId)
                        stmtInsignias.executeQuery().use { rsInsignias ->
                            while (rsInsignias.next()) {
                                insignias.add(rsInsignias.getString("nombre"))
                            }
                        }
                    }

                    // 4. Próximas actividades
                    conn.prepareStatement("""
                        SELECT nombre FROM core_actividad
                        WHERE estado = 1 AND fecha_inicio > CURDATE()
                        ORDER BY fecha_inicio ASC LIMIT 5
                    """.trimIndent()).use { stmtActividades ->
                        stmtActividades.executeQuery().use { rsActividades ->
                            while (rsActividades.next()) {
                                actividades.add(rsActividades.getString("nombre"))
                            }
                        }
                    }

                    // 5. Noticias
                    conn.prepareStatement("""
                        SELECT titulo FROM core_noticia
                        ORDER BY actualizados_en DESC LIMIT 5
                    """.trimIndent()).use { stmtNoticias ->
                        stmtNoticias.executeQuery().use { rsNoticias ->
                            while (rsNoticias.next()) {
                                noticias.add(rsNoticias.getString("titulo"))
                            }
                        }
                    }

                    // 6. Avance del barrio
                    conn.prepareStatement("""
                        SELECT progreso FROM core_progresobarrio
                        WHERE barrio_id = ?
                        ORDER BY ultima_actualizacion DESC LIMIT 1
                    """.trimIndent()).use { stmtAvance ->
                        stmtAvance.setLong(1, barrioId)
                        stmtAvance.executeQuery().use { rsAvance ->
                            if (rsAvance.next()) {
                                avance = rsAvance.getDouble("progreso")
                            }
                        }
                    }

                    // 7. Retornar objeto completo
                    return InfoVoluntario(
                        nombreCompleto = nombreCompleto,
                        puntosTotales = puntosTotales,
                        insignias = insignias,
                        proximasActividades = actividades,
                        noticias = noticias,
                        avancePorcentaje = avance
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            conn.close()
        }
    }
}
