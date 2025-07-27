package com.example.app_practicas_m5a.data.dao


import android.util.Log
import com.example.app_practicas_m5a.data.PerfilCompletoModel.PerfilCompletoModel
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

object CoreUsuarioDao {

    private fun getConexion(): Connection? = MySqlConexion.getConexion()

    // ✅ Registrar usuario con usuario y cédula
    fun registrarUsuario(usuario: CoreUsuarioModel): Boolean {
        val conn = getConexion() ?: return false
        val sql = """
            INSERT INTO core_usuario
            (email, contraseña, tipo_usuario, fecha_registro, estado, nombres, apellidos, cedula, telefono, direccion, barrio_id, usuario, puntos)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        return try {
            conn.prepareStatement(sql).use { ps ->
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
                if (usuario.barrio_id != null) ps.setLong(11, usuario.barrio_id!!) else ps.setNull(11, Types.BIGINT)
                ps.setString(12, usuario.usuario)
                ps.setInt(13, usuario.puntos)

                ps.executeUpdate() > 0
            }
        } catch (ex: Exception) {
            Log.e("CoreUsuarioDao", "Error al registrar usuario: ${ex.message}")
            false
        } finally {
            conn.close()
        }
    }

    // ✅ Login con usuario y contraseña
    fun login(usuario: String, password: String): CoreUsuarioModel? {
        val conn = getConexion() ?: return null
        var user: CoreUsuarioModel? = null
        val sql = """
            SELECT * FROM core_usuario 
            WHERE usuario = ? AND contraseña = ? AND estado = 1 LIMIT 1
        """.trimIndent()

        try {
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, usuario)
                ps.setString(2, password)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        user = mapResultSetToUsuario(rs)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            conn.close()
        }
        return user
    }

    // ✅ Obtener usuario por su nombre de usuario
    fun obtenerUsuarioPorUsuario(usuario: String): CoreUsuarioModel? {
        val conn = getConexion() ?: return null
        var user: CoreUsuarioModel? = null
        val sql = """
            SELECT * FROM core_usuario 
            WHERE usuario = ? AND estado = 1 LIMIT 1
        """.trimIndent()

        try {
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, usuario)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        user = mapResultSetToUsuario(rs)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
        return user
    }

    // ✅ Actualizar perfil
    fun actualizarPerfil(usuario: CoreUsuarioModel): Boolean {
        val conn = getConexion() ?: return false
        val sql = """
            UPDATE core_usuario
            SET nombres = ?, apellidos = ?, email = ?, telefono = ?, direccion = ?, barrio_id = ?
            WHERE usuario = ? AND estado = 1
        """.trimIndent()

        return try {
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, usuario.nombres)
                ps.setString(2, usuario.apellidos)
                ps.setString(3, usuario.email)
                ps.setString(4, usuario.telefono)
                ps.setString(5, usuario.direccion)
                if (usuario.barrio_id != null) ps.setLong(6, usuario.barrio_id!!) else ps.setNull(6, Types.BIGINT)
                ps.setString(7, usuario.usuario)
                ps.executeUpdate() > 0
            }
        } catch (ex: Exception) {
            Log.e("CoreUsuarioDao", "Error al actualizar perfil: ${ex.message}")
            false
        } finally {
            conn.close()
        }
    }

    // ✅ Obtener voluntarios por usuario líder
    fun obtenerVoluntariosPorUsuarioLider(usuarioLider: String): List<CoreUsuarioModel> {
        val conn = getConexion() ?: return emptyList()
        val voluntarios = mutableListOf<CoreUsuarioModel>()

        try {
            // 1. Obtener barrio_id del líder
            val barrioId = obtenerBarrioPorUsuario(usuarioLider, conn) ?: return emptyList()

            // 2. Obtener voluntarios del mismo barrio
            val sql = """
                SELECT * FROM core_usuario 
                WHERE barrio_id = ? AND tipo_usuario = 'ciudadano' AND estado = 1
            """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.setLong(1, barrioId)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        voluntarios.add(mapResultSetToUsuario(rs))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }

        return voluntarios
    }

    // ✅ Obtener todos los líderes activos
    fun obtenerLideres(): List<CoreUsuarioModel> {
        val conn = getConexion() ?: return emptyList()
        val lista = mutableListOf<CoreUsuarioModel>()
        val sql = "SELECT * FROM core_usuario WHERE tipo_usuario = 'lider' AND estado = 1"

        try {
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        lista.add(mapResultSetToUsuario(rs))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
        return lista
    }

    // ✅ Obtener ciudadanos por barrio
    fun obtenerCiudadanosPorBarrio(barrioId: Long): List<CoreUsuarioModel> {
        val conn = getConexion() ?: return emptyList()
        val lista = mutableListOf<CoreUsuarioModel>()
        val sql = """
            SELECT * FROM core_usuario 
            WHERE tipo_usuario = 'ciudadano' AND barrio_id = ? AND estado = 1
        """.trimIndent()

        try {
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, barrioId)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        lista.add(mapResultSetToUsuario(rs))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
        return lista
    }

    // ✅ Obtener perfil completo por usuario
    fun obtenerPerfilCompletoPorUsuario(usuario: String): PerfilCompletoModel? {
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
                u.usuario,
                u.barrio_id,
                u.puntos,
                p.id AS parroquia_id,
                c.id AS ciudad_id,
                b.nombre AS nombre_barrio,
                p.nombre AS nombre_parroquia,
                c.nombre AS nombre_ciudad
            FROM core_usuario u
            JOIN core_barrio b ON u.barrio_id = b.id
            JOIN core_parroquia p ON b.parroquia_id = p.id
            JOIN core_ciudad c ON p.ciudad_id = c.id
            WHERE u.usuario = ? AND u.estado = 1
            LIMIT 1
        """.trimIndent()

            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, usuario)
                ps.executeQuery().use { rs ->
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
                            usuario = rs.getString("usuario"),
                            barrio_id = rs.getLong("barrio_id"),
                            parroquia_id = rs.getLong("parroquia_id"),
                            ciudad_id = rs.getLong("ciudad_id"),
                            nombreBarrio = rs.getString("nombre_barrio"),
                            nombreParroquia = rs.getString("nombre_parroquia"),
                            nombreCiudad = rs.getString("nombre_ciudad"),
                            puntos = rs.getInt("puntos")
                        )
                    }
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            conn.close()
        }
        return perfil
    }

    // ✅ Helpers
    private fun mapResultSetToUsuario(rs: ResultSet): CoreUsuarioModel {
        return CoreUsuarioModel(
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
            barrio_id = rs.getLong("barrio_id").takeIf { !rs.wasNull() },
            usuario = rs.getString("usuario"),
            puntos = rs.getInt("puntos")
        )
    }

    private fun obtenerBarrioPorUsuario(usuario: String, conn: Connection): Long? {
        val sql = "SELECT barrio_id FROM core_usuario WHERE usuario = ? AND estado = 1"
        conn.prepareStatement(sql).use { ps ->
            ps.setString(1, usuario)
            ps.executeQuery().use { rs ->
                return if (rs.next()) rs.getLong("barrio_id") else null
            }
        }
    }

    private fun fromResultSet(rs: java.sql.ResultSet): CoreUsuarioModel {
        return CoreUsuarioModel(
            id = rs.getLong("id"),
            email = rs.getString("email") ?: "",
            contraseña = rs.getString("contraseña") ?: "",
            tipo_usuario = rs.getString("tipo_usuario") ?: "",
            fecha_registro = rs.getDate("fecha_registro"),
            estado = rs.getBoolean("estado"),
            nombres = rs.getString("nombres") ?: "",
            apellidos = rs.getString("apellidos") ?: "",
            cedula = rs.getString("cedula") ?: "",
            telefono = rs.getString("telefono") ?: "",
            direccion = rs.getString("direccion") ?: "",
            fecha_nacimiento = rs.getDate("fecha_nacimiento"),
            barrio_id = rs.getLong("barrio_id").takeIf { !rs.wasNull() },
            usuario = rs.getString("usuario") ?: "",
            puntos = rs.getInt("puntos")
        )
    }



    fun obtenerCiudadanosPorLider(usuarioLider: String): List<CoreUsuarioModel> {
        val lista = mutableListOf<CoreUsuarioModel>()
        val sql = """
            SELECT * FROM core_usuario 
            WHERE tipo_usuario = 'ciudadano'
              AND barrio_id = (
                SELECT barrio_id FROM core_usuario WHERE usuario = ?
              )
              AND estado = 1
        """
        val conexion = MySqlConexion.getConexion() ?: return emptyList()
        conexion.use { conn ->
            val ps = conn.prepareStatement(sql)
            ps.setString(1, usuarioLider)
            val rs = ps.executeQuery()
            while (rs.next()) {
                lista.add(fromResultSet(rs))
            }
        }
        return lista
    }

}
