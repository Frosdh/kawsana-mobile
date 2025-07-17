package com.example.app_practicas_m5a.data.dao

import com.example.app_practicas_m5a.data.model.Organizacion
import com.example.app_practicas_m5a.data.model.Proyectos

object ProyectoDisponobleDao {

    fun obtenerTodosLosProyectos(): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion()
        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, organizacion_id, progreso 
            FROM core_proyecto
        """.trimIndent()

        val statement = conexion?.prepareStatement(query)
        val resultado = statement?.executeQuery()

        while (resultado?.next() == true) {
            val id = resultado.getLong("id")
            val nombre = resultado.getString("nombre")
            val descripcion = resultado.getString("descripcion")
            val fechaInicio = resultado.getDate("fecha_inicio")
            val fechaFin = resultado.getDate("fecha_fin")
            val organizacionId = resultado.getLong("organizacion_id")
            val progreso = resultado.getInt("progreso")

            proyectos.add(
                Proyectos(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    fecha_inicio = fechaInicio,
                    fecha_fin = fechaFin,
                    organizacion_id = organizacionId,
                    progreso = progreso
                )
            )
        }

        resultado?.close()
        statement?.close()
        conexion?.close()
        return proyectos
    }

    fun obtenerProyectosPorOrganizacionId(organizacionId: Long): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion()
        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, organizacion_id, progreso 
            FROM core_proyecto
            WHERE organizacion_id = ?
        """.trimIndent()

        val statement = conexion?.prepareStatement(query)
        statement?.setLong(1, organizacionId)
        val resultado = statement?.executeQuery()

        while (resultado?.next() == true) {
            val id = resultado.getLong("id")
            val nombre = resultado.getString("nombre")
            val descripcion = resultado.getString("descripcion")
            val fechaInicio = resultado.getDate("fecha_inicio")
            val fechaFin = resultado.getDate("fecha_fin")
            val orgId = resultado.getLong("organizacion_id")
            val progreso = resultado.getInt("progreso")

            proyectos.add(
                Proyectos(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    fecha_inicio = fechaInicio,
                    fecha_fin = fechaFin,
                    organizacion_id = orgId,
                    progreso = progreso
                )
            )
        }

        resultado?.close()
        statement?.close()
        conexion?.close()

        return proyectos
    }

    fun obtenerOrganizacionPorId(id: Long): Organizacion? {
        val conexion = MySqlConexion.getConexion()
        val query = "SELECT * FROM core_organizacion WHERE id = ?"
        val statement = conexion?.prepareStatement(query)
        statement?.setLong(1, id)
        val rs = statement?.executeQuery()

        var organizacion: Organizacion? = null
        if (rs?.next() == true) {
            organizacion = Organizacion(
                id = rs.getLong("id"),
                nombre = rs.getString("nombre"),
                direccion = rs.getString("direccion"),
                telefono_contacto = rs.getString("telefono_contacto"),
                representante = rs.getString("representante")
            )
        }

        rs?.close()
        statement?.close()
        conexion?.close()

        return organizacion
    }

    fun obtenerProyectoYRelacionadosPorId(proyectoId: Long): Pair<Proyectos?, List<Proyectos>> {
        val conexion = MySqlConexion.getConexion()
        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, organizacion_id, progreso 
            FROM core_proyecto
            WHERE id = ?
        """.trimIndent()

        var proyecto: Proyectos? = null
        var proyectosRelacionados: List<Proyectos> = emptyList()

        val statement = conexion?.prepareStatement(query)
        statement?.setLong(1, proyectoId)
        val rs = statement?.executeQuery()

        if (rs?.next() == true) {
            val id = rs.getLong("id")
            val nombre = rs.getString("nombre")
            val descripcion = rs.getString("descripcion")
            val fechaInicio = rs.getDate("fecha_inicio")
            val fechaFin = rs.getDate("fecha_fin")
            val organizacionId = rs.getLong("organizacion_id")
            val progreso = rs.getInt("progreso")

            proyecto = Proyectos(
                id = id,
                nombre = nombre,
                descripcion = descripcion,
                fecha_inicio = fechaInicio,
                fecha_fin = fechaFin,
                organizacion_id = organizacionId,
                progreso = progreso
            )

            proyectosRelacionados = obtenerProyectosPorOrganizacionId(organizacionId)
                .filter { it.id != proyectoId }
        }

        rs?.close()
        statement?.close()
        conexion?.close()

        return Pair(proyecto, proyectosRelacionados)
    }

    fun obtenerProyectosNoRegistradosPorEstudiante(estudianteId: Long): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion()
        val query = """
            SELECT p.id, p.nombre, p.descripcion, p.fecha_inicio, p.fecha_fin, p.organizacion_id, p.progreso
            FROM core_proyecto p
            WHERE p.id NOT IN (
                SELECT proyecto_id FROM core_estudiante_proyecto WHERE estudiante_id = ?
            )
        """.trimIndent()

        val statement = conexion?.prepareStatement(query)
        statement?.setLong(1, estudianteId)
        val resultado = statement?.executeQuery()

        while (resultado?.next() == true) {
            val id = resultado.getLong("id")
            val nombre = resultado.getString("nombre")
            val descripcion = resultado.getString("descripcion")
            val fechaInicio = resultado.getDate("fecha_inicio")
            val fechaFin = resultado.getDate("fecha_fin")
            val organizacionId = resultado.getLong("organizacion_id")
            val progreso = resultado.getInt("progreso")

            proyectos.add(
                Proyectos(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    fecha_inicio = fechaInicio,
                    fecha_fin = fechaFin,
                    organizacion_id = organizacionId,
                    progreso = progreso
                )
            )
        }

        resultado?.close()
        statement?.close()
        conexion?.close()

        return proyectos
    }

    fun obtenerTodosLosProyectosConProgreso(): List<Proyectos> {
        val proyectos = mutableListOf<Proyectos>()
        val conexion = MySqlConexion.getConexion()
        val query = """
            SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, organizacion_id, progreso
            FROM core_proyecto
        """.trimIndent()

        val statement = conexion?.prepareStatement(query)
        val resultado = statement?.executeQuery()

        while (resultado?.next() == true) {
            val id = resultado.getLong("id")
            val nombre = resultado.getString("nombre")
            val descripcion = resultado.getString("descripcion")
            val fechaInicio = resultado.getDate("fecha_inicio")
            val fechaFin = resultado.getDate("fecha_fin")
            val organizacionId = resultado.getLong("organizacion_id")
            val progreso = resultado.getInt("progreso")

            proyectos.add(
                Proyectos(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    fecha_inicio = fechaInicio,
                    fecha_fin = fechaFin,
                    organizacion_id = organizacionId,
                    progreso = progreso
                )
            )
        }

        resultado?.close()
        statement?.close()
        conexion?.close()
        return proyectos
    }
}
