package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.EvidenciaActividad
import java.sql.Connection
import java.sql.PreparedStatement

object EvidenciaDao {

    private fun getConexion(): Connection? = MySqlConexion.getConexion()

    /**
     * Inserta una evidencia en la base de datos.
     * El campo validadorId puede ser null, y en ese caso se inserta como NULL en la BD.
     */
    fun insertarEvidencia(evidencia: EvidenciaActividad): Boolean {
        val conn = getConexion() ?: return false

        val sql = """
            INSERT INTO core_evidenciaactividad 
            (archivo_url, tipo_archivo, descripcion, fecha_subida, actividad_id, usuario_id, es_valida, fecha_validacion, validador_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        var ps: PreparedStatement? = null
        return try {
            ps = conn.prepareStatement(sql)
            ps.setString(1, evidencia.archivoUrl)
            ps.setString(2, evidencia.tipoArchivo)
            ps.setString(3, evidencia.descripcion)
            ps.setDate(4, java.sql.Date.valueOf(evidencia.fechaSubida))  // Asumiendo fechaSubida como String "yyyy-MM-dd"
            ps.setLong(5, evidencia.actividadId)
            ps.setLong(6, evidencia.usuarioId)
            ps.setBoolean(7, evidencia.esValida)

            // fechaValidacion puede ser null
            if (evidencia.fechaValidacion != null) {
                ps.setDate(8, java.sql.Date.valueOf(evidencia.fechaValidacion))
            } else {
                ps.setNull(8, java.sql.Types.DATE)
            }

            // validadorId puede ser null
            if (evidencia.validadorId != null) {
                ps.setLong(9, evidencia.validadorId!!)
            } else {
                ps.setNull(9, java.sql.Types.BIGINT)
            }

            val res = ps.executeUpdate()
            res > 0
        } catch (ex: Exception) {
            Log.e("EvidenciaDao", "Error al insertar evidencia: ${ex.message}")
            ex.printStackTrace()
            false
        } finally {
            ps?.close()
            conn.close()
        }
    }

}

