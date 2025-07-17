package com.example.app_practicas_m5a.data.dao

import android.util.Log

object InscripcionProyectoDao {

    fun estaInscrito(usuarioId: Long, proyectoId: Long): Boolean {
        val conexion = MySqlConexion.getConexion() ?: return false
        val sql = "SELECT COUNT(*) FROM core_liderproyectobarrio WHERE usuario_id = ? AND proyecto_id = ?"

        return try {
            val statement = conexion.prepareStatement(sql)
            statement.setLong(1, usuarioId)
            statement.setLong(2, proyectoId)
            val resultado = statement.executeQuery()
            val inscrito = resultado.next() && resultado.getInt(1) > 0

            resultado.close()
            statement.close()
            conexion.close()
            inscrito
        } catch (e: Exception) {
            Log.e("InscripcionProyectoDao", "Error al verificar inscripción: ${e.message}")
            false
        }
    }

    fun registrarInscripcion(usuarioId: Long, barrioId: Long, proyectoId: Long): Boolean {
        val conexion = MySqlConexion.getConexion() ?: return false
        val sql = """
            INSERT INTO core_liderproyectobarrio (usuario_id, barrio_id, proyecto_id) 
            VALUES (?, ?, ?)
        """.trimIndent()

        return try {
            val statement = conexion.prepareStatement(sql)
            statement.setLong(1, usuarioId)
            statement.setLong(2, barrioId)
            statement.setLong(3, proyectoId)
            val filasInsertadas = statement.executeUpdate()

            statement.close()
            conexion.close()

            filasInsertadas > 0
        } catch (e: Exception) {
            Log.e("InscripcionProyectoDao", "Error al registrar inscripción: ${e.message}")
            false
        }
    }

   suspend fun yaRegistrado(usuarioId: Long, proyectoId: Long): Boolean {
        return estaInscrito(usuarioId, proyectoId)
    }
}
