package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.EvidenciaActividad
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

object EvidenciaDao {

    private fun getConexion(): Connection? = MySqlConexion.getConexion()

    /**
     * Inserta una evidencia en la base de datos.
     */
    fun insertarEvidencia(evidencia: EvidenciaActividad): Boolean {
        val conn = getConexion() ?: return false

        val sql = """
        INSERT INTO core_evidenciaactividad 
        (descripcion, fecha_subida, actividad_id, usuario_id, es_valida, fecha_validacion, validador_id, archivo, estado, puntos)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()

        var ps: PreparedStatement? = null
        return try {
            ps = conn.prepareStatement(sql)
            ps.setString(1, evidencia.descripcion)
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(evidencia.fechaSubida)) // yyyy-MM-dd HH:mm:ss
            ps.setLong(3, evidencia.actividadId)
            ps.setLong(4, evidencia.usuarioId)
            ps.setBoolean(5, evidencia.esValida)

            // Si fechaValidacion es null o vacía, asignar fecha por defecto "1970-01-01 00:00:00"
            val fechaValidacion = if (evidencia.fechaValidacion.isNullOrEmpty()) {
                "1970-01-01 00:00:00"
            } else {
                evidencia.fechaValidacion
            }
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(fechaValidacion))

            // Validador ID
            if (evidencia.validadorId != null) {
                ps.setLong(7, evidencia.validadorId)
            } else {
                ps.setNull(7, java.sql.Types.BIGINT)
            }

            ps.setString(8, evidencia.archivo)
            ps.setString(9, evidencia.estado)
            ps.setInt(10, evidencia.puntos)

            ps.executeUpdate() > 0
        } catch (ex: Exception) {
            Log.e("EvidenciaDao", "Error al insertar evidencia: ${ex.message}")
            false
        } finally {
            ps?.close()
            conn.close()
        }
    }
    fun insertarNuevaEvidencia(evidencia: EvidenciaActividad): Boolean {
        val conn = getConexion() ?: return false

        val sql = """
        INSERT INTO core_evidenciaactividad 
        (descripcion, fecha_subida, actividad_id, usuario_id, es_valida, fecha_validacion, validador_id, archivo, estado, puntos)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()

        var ps: PreparedStatement? = null
        return try {
            ps = conn.prepareStatement(sql)
            ps.setString(1, evidencia.descripcion)
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(evidencia.fechaSubida)) // yyyy-MM-dd HH:mm:ss
            ps.setLong(3, evidencia.actividadId)
            ps.setLong(4, evidencia.usuarioId)
            ps.setBoolean(5, evidencia.esValida)

            // Enviar una fecha por defecto si fechaValidacion es null o vacía
            val fechaValidacion = if (evidencia.fechaValidacion.isNullOrEmpty()) {
                "1970-01-01 00:00:00"
            } else {
                evidencia.fechaValidacion
            }
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(fechaValidacion))

            // validadorId puede ser null, en ese caso mandar NULL
            if (evidencia.validadorId != null) {
                ps.setLong(7, evidencia.validadorId)
            } else {
                ps.setNull(7, java.sql.Types.BIGINT)
            }

            ps.setString(8, evidencia.archivo)
            ps.setString(9, evidencia.estado)
            ps.setInt(10, evidencia.puntos)

            ps.executeUpdate() > 0
        } catch (ex: Exception) {
            Log.e("EvidenciaDao", "Error al insertar nueva evidencia: ${ex.message}")
            false
        } finally {
            ps?.close()
            conn.close()
        }
    }

    fun actualizarEstadoActividad(actividadId: Long, nuevoEstado: Int = 1): Boolean {
        val conn = getConexion() ?: return false
        val sql = "UPDATE core_actividad SET estado = ? WHERE id = ?"
        var ps: PreparedStatement? = null
        return try {
            ps = conn.prepareStatement(sql)
            ps.setInt(1, nuevoEstado)
            ps.setLong(2, actividadId)
            ps.executeUpdate() > 0
        } catch (ex: Exception) {
            Log.e("EvidenciaDao", "Error actualizando estado actividad: ${ex.message}")
            false
        } finally {
            ps?.close()
            conn.close()
        }
    }


    fun obtenerEvidenciasDelLider(cedulaLider: String): List<EvidenciaActividad> {
        val listaEvidencias = mutableListOf<EvidenciaActividad>()
        val conn = getConexion() ?: return listaEvidencias

        val sql = """
            SELECT ea.id, ea.descripcion, ea.fecha_subida, ea.actividad_id, ea.usuario_id, 
                   ea.es_valida, ea.fecha_validacion, ea.validador_id, ea.archivo, ea.estado, ea.puntos
            FROM core_evidenciaactividad ea
            JOIN core_actividad a ON ea.actividad_id = a.id
            JOIN core_proyecto p ON a.proyecto_id = p.id
            JOIN core_liderproyectobarrio lp ON p.id = lp.proyecto_id
            JOIN core_usuario u ON lp.usuario_id = u.id
            WHERE u.cedula = ?
            ORDER BY ea.fecha_subida DESC
        """.trimIndent()

        var ps: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            ps = conn.prepareStatement(sql)
            ps.setString(1, cedulaLider)
            rs = ps.executeQuery()

            while (rs.next()) {
                val evidencia = EvidenciaActividad(
                    id = rs.getLong("id"),
                    descripcion = rs.getString("descripcion"),
                    fechaSubida = rs.getString("fecha_subida"),   // String "yyyy-MM-dd HH:mm:ss"
                    actividadId = rs.getLong("actividad_id"),
                    usuarioId = rs.getLong("usuario_id"),
                    esValida = rs.getBoolean("es_valida"),
                    fechaValidacion = rs.getString("fecha_validacion"),
                    validadorId = rs.getLong("validador_id").takeIf { !rs.wasNull() },
                    archivo = rs.getString("archivo"),
                    estado = rs.getString("estado"),
                    puntos = rs.getInt("puntos")
                )
                listaEvidencias.add(evidencia)
            }
        } catch (ex: Exception) {
            Log.e("EvidenciaDao", "Error al obtener evidencias del líder: ${ex.message}")
            ex.printStackTrace()
        } finally {
            rs?.close()
            ps?.close()
            conn.close()
        }

        return listaEvidencias
    }

}
