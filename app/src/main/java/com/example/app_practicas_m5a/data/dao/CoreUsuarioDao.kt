package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import com.example.app_practicas_m5a.data.model.InfoVoluntario
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Date
import java.util.*

object CoreUsuarioDao {

    private fun getConexion(): Connection? = MySqlConexion.getConexion()

    fun registrarUsuario(usuario: CoreUsuarioModel): Boolean {
        val conn = getConexion() ?: return false

        val sql = """
            INSERT INTO core_usuario
            (email, contraseña, tipo_usuario, fecha_registro, estado, nombres, apellidos, cedula, telefono, direccion)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        var ps: PreparedStatement? = null
        return try {
            ps = conn.prepareStatement(sql)
            ps.setString(1, usuario.email)
            ps.setString(2, usuario.contraseña)
            ps.setString(3, usuario.tipo_usuario)
            ps.setDate(4, java.sql.Date(usuario.fecha_registro.time))
            ps.setBoolean(5, usuario.estado)
            ps.setString(6, usuario.nombres)
            ps.setString(7, usuario.apellidos)
            ps.setString(8, usuario.cedula)
            ps.setString(9, usuario.telefono)
            ps.setString(10, usuario.direccion)

            val res = ps.executeUpdate()
            res > 0
        } catch (ex: Exception) {
            Log.e("CoreUsuarioDao", "Error al registrar usuario: ${ex.message}")
            false
        } finally {
            ps?.close()
            conn.close()
        }
    }
    fun login(cedula: String, password: String): CoreUsuarioModel? {
        val conn = getConexion()
        var user: CoreUsuarioModel? = null
        try {
            val sql = "SELECT * FROM core_usuario WHERE cedula = ? AND contraseña = ? AND estado = 1 LIMIT 1"
            val ps = conn?.prepareStatement(sql)
            ps?.setString(1, cedula)
            ps?.setString(2, password)
            val rs = ps?.executeQuery()
            if (rs != null && rs.next()) {
                user = CoreUsuarioModel(
                    id = rs.getLong("id"),
                    email = rs.getString("email"),
                    contraseña = rs.getString("contraseña"),
                    tipo_usuario = rs.getString("tipo_usuario"),
                    fecha_registro = rs.getDate("fecha_registro"),
                    estado = rs.getBoolean("estado"),
                    nombres = rs.getString("nombres"),
                    apellidos = rs.getString("apellidos"),
                    cedula = rs.getString("cedula"),
                    telefono = rs.getString("telefono"),
                    direccion = rs.getString("direccion"),
                    fecha_nacimiento = rs.getDate("fecha_nacimiento"),
                    barrio_id = rs.getLong("barrio_id")
                )
            }
            rs?.close()
            ps?.close()
            conn?.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return user
    }



    object InfoVoluntarioDao {

        private const val URL = "jdbc:mysql://<tu_host>:<puerto>/<tu_bd>"
        private const val USER = "<usuario>"
        private const val PASSWORD = "<password>"

        private fun getConnection(): Connection? {
            return try {
                DriverManager.getConnection(URL, USER, PASSWORD)
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }
        }

        fun obtenerInfoPorCedula(cedula: String): InfoVoluntario? {
            val conn = getConnection() ?: return null

            try {
                // 1. Obtener nombre completo y barrio_id
                val psUser = conn.prepareStatement(
                    "SELECT nombres, apellidos, barrio_id FROM core_usuario WHERE cedula = ? AND estado = 1"
                )
                psUser.setString(1, cedula)
                val rsUser = psUser.executeQuery()
                if (!rsUser.next()) return null
                val nombreCompleto = "${rsUser.getString("nombres")} ${rsUser.getString("apellidos")}"
                val barrioId = rsUser.getLong("barrio_id")
                rsUser.close()
                psUser.close()

                // 2. Obtener puntos totales (ejemplo simplificado: suma puntos actividades activas)
                val psPuntos = conn.prepareStatement(
                    "SELECT SUM(puntos) as total_puntos FROM core_actividad WHERE estado = 1"
                )
                val rsPuntos = psPuntos.executeQuery()
                val puntosTotales = if (rsPuntos.next()) rsPuntos.getInt("total_puntos") else 0
                rsPuntos.close()
                psPuntos.close()

                // 3. Obtener insignias
                val psInsignias = conn.prepareStatement(
                    """SELECT i.nombre FROM core_usuarioinsignia ui
                   JOIN core_insignia i ON ui.insignia_id = i.id
                   JOIN core_usuario u ON ui.usuario_id = u.id
                   WHERE u.cedula = ?"""
                )
                psInsignias.setString(1, cedula)
                val rsInsignias = psInsignias.executeQuery()
                val insignias = mutableListOf<String>()
                while (rsInsignias.next()) {
                    insignias.add(rsInsignias.getString("nombre"))
                }
                rsInsignias.close()
                psInsignias.close()

                // 4. Próximas actividades
                val psActividades = conn.prepareStatement(
                    "SELECT nombre FROM core_actividad WHERE estado = 1 AND fecha_inicio >= CURDATE() ORDER BY fecha_inicio LIMIT 5"
                )
                val rsActividades = psActividades.executeQuery()
                val actividades = mutableListOf<String>()
                while (rsActividades.next()) {
                    actividades.add(rsActividades.getString("nombre"))
                }
                rsActividades.close()
                psActividades.close()

                // 5. Noticias recientes
                val psNoticias = conn.prepareStatement(
                    "SELECT titulo FROM core_noticia ORDER BY actualizados_en DESC LIMIT 5"
                )
                val rsNoticias = psNoticias.executeQuery()
                val noticias = mutableListOf<String>()
                while (rsNoticias.next()) {
                    noticias.add(rsNoticias.getString("titulo"))
                }
                rsNoticias.close()
                psNoticias.close()

                // 6. Avance del barrio
                val psAvance = conn.prepareStatement(
                    "SELECT progreso FROM core_progresobarrio WHERE barrio_id = ? ORDER BY ultima_actualizacion DESC LIMIT 1"
                )
                psAvance.setLong(1, barrioId)
                val rsAvance = psAvance.executeQuery()
                val avance = if (rsAvance.next()) rsAvance.getDouble("progreso") else 0.0
                rsAvance.close()
                psAvance.close()

                conn.close()

                return InfoVoluntario(
                    nombreCompleto = nombreCompleto,
                    puntosTotales = puntosTotales,
                    insignias = insignias,
                    proximasActividades = actividades,
                    noticias = noticias,
                    avancePorcentaje = avance
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }
        }
    }
    fun obtenerUsuarioPorCedula(cedula: String): CoreUsuarioModel? {
        val conn = getConexion()
        var user: CoreUsuarioModel? = null
        try {
            val sql = "SELECT * FROM core_usuario WHERE cedula = ? AND estado = 1 LIMIT 1"
            val ps = conn?.prepareStatement(sql)
            ps?.setString(1, cedula)
            val rs = ps?.executeQuery()
            if (rs != null && rs.next()) {
                user = CoreUsuarioModel(
                    id = rs.getLong("id"),
                    email = rs.getString("email"),
                    contraseña = rs.getString("contraseña"),
                    tipo_usuario = rs.getString("tipo_usuario"),
                    fecha_registro = rs.getDate("fecha_registro"),
                    estado = rs.getBoolean("estado"),
                    nombres = rs.getString("nombres"),
                    apellidos = rs.getString("apellidos"),
                    cedula = rs.getString("cedula"),
                    telefono = rs.getString("telefono"),
                    direccion = rs.getString("direccion"),
                    fecha_nacimiento = rs.getDate("fecha_nacimiento"),
                    barrio_id = rs.getLong("barrio_id")
                )
            }
            rs?.close()
            ps?.close()
            conn?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return user
    }
    fun actualizarPerfil(usuario: CoreUsuarioModel): Boolean {
        val conn = getConexion() ?: run {
            Log.e("CoreUsuarioDao", "No se pudo obtener conexión a la BD")
            return false
        }

        val sql = """
        UPDATE core_usuario
        SET nombres = ?, apellidos = ?, email = ?, telefono = ?, direccion = ?
        WHERE cedula = ? AND estado = 1
    """.trimIndent()

        var ps: PreparedStatement? = null

        return try {
            ps = conn.prepareStatement(sql)
            ps.setString(1, usuario.nombres)
            ps.setString(2, usuario.apellidos)
            ps.setString(3, usuario.email)
            ps.setString(4, usuario.telefono)
            ps.setString(5, usuario.direccion)
            ps.setString(6, usuario.cedula)

            Log.d("CoreUsuarioDao", "Ejecutando update perfil para cédula: ${usuario.cedula} con nombres: ${usuario.nombres}, apellidos: ${usuario.apellidos}, email: ${usuario.email}, teléfono: ${usuario.telefono}, dirección: ${usuario.direccion}")
            val res = ps.executeUpdate()
            Log.d("CoreUsuarioDao", "Filas afectadas: $res")
            res > 0
        } catch (ex: Exception) {
            Log.e("CoreUsuarioDao", "Error al actualizar perfil: ${ex.message}")
            ex.printStackTrace()
            false
        } finally {
            ps?.close()
            conn.close()
        }
    }





}