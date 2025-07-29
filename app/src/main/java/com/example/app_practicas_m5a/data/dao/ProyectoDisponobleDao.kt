package com.example.app_practicas_m5a.data.dao

import com.example.app_practicas_m5a.data.model.Organizacion
import com.example.app_practicas_m5a.data.model.Proyectos
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

object ProyectoDisponobleDao {
    fun obtenerTodosLosProyectos(): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion() ?: return emptyList()

        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, estado, organizacion_id, progreso 
            FROM core_proyecto
        """.trimIndent()

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.executeQuery().use { resultado ->
                    while (resultado.next()) {
                        proyectos.add(
                            Proyectos(
                                id = resultado.getLong("id"),
                                nombre = resultado.getString("nombre"),
                                descripcion = resultado.getString("descripcion"),
                                fecha_inicio = resultado.getDate("fecha_inicio"),
                                fecha_fin = resultado.getDate("fecha_fin"),
                                estado = resultado.getBoolean("estado"),
                                organizacion_id = resultado.getLong("organizacion_id"),
                                progreso = resultado.getString("progreso")
                            )
                        )
                    }
                }
            }
        }

        return proyectos
    }

    fun obtenerProyectosPorOrganizacionId(organizacionId: Long): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion() ?: return emptyList()
        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, estado, organizacion_id, progreso 
            FROM core_proyecto
            WHERE organizacion_id = ?
        """.trimIndent()

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.setLong(1, organizacionId)
                statement.executeQuery().use { resultado ->
                    while (resultado.next()) {
                        proyectos.add(
                            Proyectos(
                                id = resultado.getLong("id"),
                                nombre = resultado.getString("nombre"),
                                descripcion = resultado.getString("descripcion"),
                                fecha_inicio = resultado.getDate("fecha_inicio"),
                                fecha_fin = resultado.getDate("fecha_fin"),
                                estado = resultado.getBoolean("estado"),
                                organizacion_id = resultado.getLong("organizacion_id"),
                                progreso = resultado.getString("progreso")
                            )
                        )
                    }
                }
            }
        }

        return proyectos
    }

    fun obtenerOrganizacionPorId(id: Long): Organizacion? {
        val conexion = MySqlConexion.getConexion() ?: return null
        val query = "SELECT * FROM core_organizacion WHERE id = ?"

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.setLong(1, id)
                statement.executeQuery().use { rs ->
                    if (rs.next()) {
                        return Organizacion(
                            id = rs.getLong("id"),
                            nombre = rs.getString("nombre"),
                            direccion = rs.getString("direccion"),
                            telefono_contacto = rs.getString("telefono_contacto"),
                            email_contacto = rs.getString("email_contacto"),
                            representante = rs.getString("representante"),
                            ruc = rs.getString("ruc")
                        )
                    }
                }
            }
        }

        return null
    }

    fun obtenerProyectoYRelacionadosPorId(proyectoId: Long): Pair<Proyectos?, List<Proyectos>> {
        val conexion = MySqlConexion.getConexion() ?: return Pair(null, emptyList())
        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, estado, organizacion_id, progreso 
            FROM core_proyecto
            WHERE id = ?
        """.trimIndent()

        var proyecto: Proyectos? = null
        var proyectosRelacionados: List<Proyectos> = emptyList()

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.setLong(1, proyectoId)
                statement.executeQuery().use { rs ->
                    if (rs.next()) {
                        proyecto = Proyectos(
                            id = rs.getLong("id"),
                            nombre = rs.getString("nombre"),
                            descripcion = rs.getString("descripcion"),
                            fecha_inicio = rs.getDate("fecha_inicio"),
                            fecha_fin = rs.getDate("fecha_fin"),
                            estado = rs.getBoolean("estado"),
                            organizacion_id = rs.getLong("organizacion_id"),
                            progreso = rs.getString("progreso")
                        )
                        proyectosRelacionados = obtenerProyectosPorOrganizacionId(proyecto!!.organizacion_id)
                            .filter { it.id != proyectoId }
                    }
                }
            }
        }

        return Pair(proyecto, proyectosRelacionados)
    }

    fun obtenerProyectosNoRegistradosPorEstudiante(estudianteId: Long): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion() ?: return emptyList()
        val query = """
            SELECT p.id, p.nombre, p.descripcion, p.fecha_inicio, p.fecha_fin, p.estado, p.organizacion_id, p.progreso
            FROM core_proyecto p
            WHERE p.id NOT IN (
                SELECT proyecto_id FROM core_estudiante_proyecto WHERE estudiante_id = ?
            )
        """.trimIndent()

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.setLong(1, estudianteId)
                statement.executeQuery().use { resultado ->
                    while (resultado.next()) {
                        proyectos.add(
                            Proyectos(
                                id = resultado.getLong("id"),
                                nombre = resultado.getString("nombre"),
                                descripcion = resultado.getString("descripcion"),
                                fecha_inicio = resultado.getDate("fecha_inicio"),
                                fecha_fin = resultado.getDate("fecha_fin"),
                                estado = resultado.getBoolean("estado"),
                                organizacion_id = resultado.getLong("organizacion_id"),
                                progreso = resultado.getString("progreso")
                            )
                        )
                    }
                }
            }
        }

        return proyectos
    }

    fun obtenerTodosLosProyectosConProgreso(): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion() ?: return emptyList()
        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, estado, organizacion_id, progreso
            FROM core_proyecto
        """.trimIndent()

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.executeQuery().use { resultado ->
                    while (resultado.next()) {
                        proyectos.add(
                            Proyectos(
                                id = resultado.getLong("id"),
                                nombre = resultado.getString("nombre"),
                                descripcion = resultado.getString("descripcion"),
                                fecha_inicio = resultado.getDate("fecha_inicio"),
                                fecha_fin = resultado.getDate("fecha_fin"),
                                estado = resultado.getBoolean("estado"),
                                organizacion_id = resultado.getLong("organizacion_id"),
                                progreso = resultado.getString("progreso")
                            )
                        )
                    }
                }
            }
        }

        return proyectos
    }

    fun obtenerProyectosAgrupadosPorEstado(): Pair<List<Proyectos>, List<Proyectos>> {
        val proyectosActivos = mutableListOf<Proyectos>()
        val proyectosInactivos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion() ?: return Pair(emptyList(), emptyList())

        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, estado, organizacion_id, progreso
            FROM core_proyecto
            ORDER BY fecha_inicio DESC
        """.trimIndent()

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.executeQuery().use { resultado ->
                    while (resultado.next()) {
                        val estadoBoolean = resultado.getBoolean("estado")
                        val proyecto = Proyectos(
                            id = resultado.getLong("id"),
                            nombre = resultado.getString("nombre"),
                            descripcion = resultado.getString("descripcion"),
                            fecha_inicio = resultado.getDate("fecha_inicio"),
                            fecha_fin = resultado.getDate("fecha_fin"),
                            estado = estadoBoolean,
                            organizacion_id = resultado.getLong("organizacion_id"),
                            progreso = resultado.getString("progreso")
                        )
                        if (estadoBoolean) proyectosActivos.add(proyecto)
                        else proyectosInactivos.add(proyecto)
                    }
                }
            }
        }
        return Pair(proyectosActivos, proyectosInactivos)
    }
    fun obtenerProyectosPorEstadoYProgreso(): Triple<List<Proyectos>, List<Proyectos>, List<Proyectos>> {
        val conexion = MySqlConexion.getConexion() ?: return Triple(emptyList(), emptyList(), emptyList())
        val activos = mutableListOf<Proyectos>()
        val inactivos = mutableListOf<Proyectos>()
        val pendientes = mutableListOf<Proyectos>()

        val query = """
        SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, estado, organizacion_id, progreso
        FROM core_proyecto
        ORDER BY fecha_inicio DESC
    """.trimIndent()

        conexion.use { conn ->
            conn.prepareStatement(query).use { statement ->
                statement.executeQuery().use { resultado ->
                    while (resultado.next()) {
                        val proyecto = Proyectos(
                            id = resultado.getLong("id"),
                            nombre = resultado.getString("nombre"),
                            descripcion = resultado.getString("descripcion"),
                            fecha_inicio = resultado.getDate("fecha_inicio"),
                            fecha_fin = resultado.getDate("fecha_fin"),
                            estado = resultado.getBoolean("estado"),
                            organizacion_id = resultado.getLong("organizacion_id"),
                            progreso = resultado.getString("progreso")
                        )

                        when {
                            proyecto.progreso.equals("pendiente", ignoreCase = true) -> pendientes.add(proyecto)
                            proyecto.estado -> activos.add(proyecto)
                            else -> inactivos.add(proyecto)
                        }
                    }
                }
            }
        }

        return Triple(activos, inactivos, pendientes)
    }

}