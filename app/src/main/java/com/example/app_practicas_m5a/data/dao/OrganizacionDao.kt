package com.example.app_practicas_m5a.data.dao

import com.example.app_practicas_m5a.data.model.Organizacion
import java.sql.Connection

object OrganizacionDao {

    fun obtenerTodasOrganizaciones(): List<Organizacion> {
        val lista = mutableListOf<Organizacion>()
        val conn: Connection? = MySqlConexion.getConexion()

        if (conn != null) {
            try {
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery("SELECT id, nombre, direccion, telefono_contacto, email_contacto, representante, ruc FROM core_organizacion")

                while (rs.next()) {
                    val org = Organizacion(
                        id = rs.getLong("id"),
                        nombre = rs.getString("nombre"),
                        direccion = rs.getString("direccion"),
                        telefono_contacto = rs.getString("telefono_contacto"),
                        email_contacto = rs.getString("email_contacto"),
                        representante = rs.getString("representante"),
                        ruc = rs.getString("ruc")
                    )
                    lista.add(org)
                }
                rs.close()
                stmt.close()
                conn.close()
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar error (log, throw, etc)
            }
        }
        return lista
    }
}
