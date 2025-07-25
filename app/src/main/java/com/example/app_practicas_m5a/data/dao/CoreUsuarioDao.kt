package com.example.app_practicas_m5a.data.dao

import PerfilCompletoModel
import android.util.Log
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Types

object CoreUsuarioDao {

    private fun getConexion(): Connection? = MySqlConexion.getConexion()

    fun registrarUsuario(usuario: CoreUsuarioModel): Boolean {
        val conn = getConexion() ?: return false

        val sql = """
            INSERT INTO core_usuario
            (email, contraseña, tipo_usuario, fecha_registro, estado, nombres, apellidos, cedula, telefono, direccion, barrio_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        var ps: PreparedStatement? = null

        return try {
            ps = conn.prepareStatement(sql)
            ps.setString(1, usuario.email)
            ps.setString(2, usuario.contraseña)
            ps.setString(3, usuario.tipo_usuario)
            ps.setDate(4, usuario.fecha_registro?.let { Date(it.time) })
            ps.setBoolean(5, usuario.estado)
            ps.setString(6, usuario.nombres)
            ps.setString(7, usuario.apellidos)
            ps.setString(8, usuario.cedula)
            ps.setString(9, usuario.telefono)
            ps.setString(10, usuario.direccion)

            if (usuario.barrio_id != null) {
                ps.setLong(11, usuario.barrio_id!!)
            } else {
                ps.setNull(11, Types.BIGINT)
            }

            ps.executeUpdate() > 0
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
        val conn = getConexion() ?: return false

        val sql = """
            UPDATE core_usuario
            SET nombres = ?, apellidos = ?, email = ?, telefono = ?, direccion = ?, barrio_id = ?
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

            if (usuario.barrio_id != null) {
                ps.setLong(6, usuario.barrio_id!!)
            } else {
                ps.setNull(6, Types.BIGINT)
            }

            ps.setString(7, usuario.cedula)

            ps.executeUpdate() > 0
        } catch (ex: Exception) {
            Log.e("CoreUsuarioDao", "Error al actualizar perfil: ${ex.message}")
            false
        } finally {
            ps?.close()
            conn.close()
        }
    }

    fun obtenerVoluntariosPorCedulaLider(cedulaLider: String): List<CoreUsuarioModel> {
        val conn = getConexion() ?: return emptyList()
        val voluntarios = mutableListOf<CoreUsuarioModel>()

        try {
            val psBarrio = conn.prepareStatement(
                "SELECT barrio_id FROM core_usuario WHERE cedula = ? AND estado = 1"
            )
            psBarrio.setString(1, cedulaLider)
            val rsBarrio = psBarrio.executeQuery()

            if (!rsBarrio.next()) {
                rsBarrio.close()
                psBarrio.close()
                return emptyList()
            }

            val barrioId = rsBarrio.getLong("barrio_id")
            rsBarrio.close()
            psBarrio.close()

            val psVoluntarios = conn.prepareStatement(
                "SELECT email, nombres, apellidos, telefono FROM core_usuario WHERE barrio_id = ? AND tipo_usuario = 'Voluntario' AND estado = 1"
            )
            psVoluntarios.setLong(1, barrioId)
            val rsVoluntarios = psVoluntarios.executeQuery()

            while (rsVoluntarios.next()) {
                val voluntario = CoreUsuarioModel(
                    id = 0L,
                    email = rsVoluntarios.getString("email"),
                    contraseña = "",
                    tipo_usuario = "Voluntario",
                    fecha_registro = Date(0),
                    estado = true,
                    nombres = rsVoluntarios.getString("nombres"),
                    apellidos = rsVoluntarios.getString("apellidos"),
                    cedula = "",
                    telefono = rsVoluntarios.getString("telefono"),
                    direccion = "",
                    fecha_nacimiento = null,
                    barrio_id = barrioId
                )
                voluntarios.add(voluntario)
            }

            rsVoluntarios.close()
            psVoluntarios.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }

        return voluntarios
    }

    fun obtenerPerfilCompletoPorCedula(cedula: String): PerfilCompletoModel? {
        val conn = getConexion() ?: return null
        var perfil: PerfilCompletoModel? = null

        try {
            val sql = """
                SELECT 
                    u.id AS usuario_id,
                    u.nombres,
                    u.apellidos,
                    u.cedula,
                    u.email,
                    u.telefono,
                    u.direccion,
                    u.tipo_usuario,
                    b.nombre AS nombre_barrio,
                    p.nombre AS nombre_parroquia,
                    c.nombre AS nombre_ciudad
                FROM 
                    core_usuario u
                JOIN 
                    core_barrio b ON u.barrio_id = b.id
                JOIN 
                    core_parroquia p ON b.parroquia_id = p.id
                JOIN 
                    core_ciudad c ON p.ciudad_id = c.id
                WHERE u.cedula = ? AND u.estado = 1
                LIMIT 1
            """.trimIndent()

            val ps = conn.prepareStatement(sql)
            ps.setString(1, cedula)
            val rs = ps.executeQuery()

            if (rs.next()) {
                perfil = PerfilCompletoModel(
                    id = rs.getLong("usuario_id"),
                    nombres = rs.getString("nombres"),
                    apellidos = rs.getString("apellidos"),
                    cedula = rs.getString("cedula"),
                    email = rs.getString("email"),
                    telefono = rs.getString("telefono"),
                    direccion = rs.getString("direccion"),
                    tipo_usuario = rs.getString("tipo_usuario"),
                    nombreBarrio = rs.getString("nombre_barrio"),
                    nombreParroquia = rs.getString("nombre_parroquia"),
                    nombreCiudad = rs.getString("nombre_ciudad")
                )
            }

            rs.close()
            ps.close()
            conn.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return perfil
    }
}
