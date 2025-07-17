package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.core_noticia
import java.sql.Connection

object NoticiaDao {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    fun obtenerNoticias(): List<core_noticia> {
        val noticias = mutableListOf<core_noticia>()
        val conn = getConnection() ?: return noticias

        val sql = """
            SELECT id, titulo, contenido, imagen_url, actualizados_en, autor_id
            FROM core_noticia
            ORDER BY actualizados_en DESC
        """.trimIndent()

        try {
            conn.use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            val noticia = core_noticia(
                                id = rs.getLong("id"),
                                titulo = rs.getString("titulo"),
                                contenido = rs.getString("contenido"),
                                imagenUrl = rs.getString("imagen_url"),
                                actualizadosEn = rs.getDate("actualizados_en")?.toString() ?: "",
                                autorId = rs.getLong("autor_id")
                            )
                            noticias.add(noticia)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("NoticiaDao", "Error al obtener noticias: ", ex)
        }

        return noticias
    }
}


