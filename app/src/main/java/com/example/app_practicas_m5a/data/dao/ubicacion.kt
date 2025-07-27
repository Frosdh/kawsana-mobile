package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.Barrio
import com.example.app_practicas_m5a.data.model.Ciudad
import com.example.app_practicas_m5a.data.model.Parroquia
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

object ubicacion {
    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    /** Obtiene todas las ciudades */
    fun obtenerCiudades(): List<Ciudad> {
        val ciudades = mutableListOf<Ciudad>()
        val query = "SELECT id, nombre FROM core_ciudad"

        try {
            getConnection()?.use { connection ->
                connection.createStatement().use { statement ->
                    val resultSet = statement.executeQuery(query)
                    while (resultSet.next()) {
                        ciudades.add(
                            Ciudad(
                                id = resultSet.getInt("id"),
                                nombre = resultSet.getString("nombre")
                            )
                        )
                    }
                    resultSet.close()
                }
            }
        } catch (e: SQLException) {
            Log.e("UbicacionDao", "Error al obtener ciudades: ${e.message}")
        }

        return ciudades
    }

    /** Obtiene parroquias según la ciudad */
    fun obtenerParroquiasPorCiudad(ciudadId: Int): List<Parroquia> {
        val parroquias = mutableListOf<Parroquia>()
        val query = "SELECT id, nombre, ciudad_id FROM core_parroquia WHERE ciudad_id = ?"

        try {
            getConnection()?.use { connection ->
                connection.prepareStatement(query).use { preparedStatement ->
                    preparedStatement.setInt(1, ciudadId)
                    val resultSet: ResultSet = preparedStatement.executeQuery()
                    while (resultSet.next()) {
                        parroquias.add(
                            Parroquia(
                                id = resultSet.getInt("id"),
                                nombre = resultSet.getString("nombre"),
                                ciudad_id = resultSet.getInt("ciudad_id")
                            )
                        )
                    }
                    resultSet.close()
                }
            }
        } catch (e: SQLException) {
            Log.e("UbicacionDao", "Error al obtener parroquias: ${e.message}")
        }

        return parroquias
    }

    /** Obtiene barrios según la parroquia */
    fun obtenerBarriosPorParroquia(parroquiaId: Int): List<Barrio> {
        val barrios = mutableListOf<Barrio>()
        val query = "SELECT id, nombre, num_habitantes, parroquia_id FROM core_barrio WHERE parroquia_id = ?"

        try {
            getConnection()?.use { connection ->
                connection.prepareStatement(query).use { preparedStatement ->
                    preparedStatement.setInt(1, parroquiaId)
                    val resultSet: ResultSet = preparedStatement.executeQuery()
                    while (resultSet.next()) {
                        barrios.add(
                            Barrio(
                                id = resultSet.getInt("id"),
                                nombre = resultSet.getString("nombre"),
                                num_habitantes = resultSet.getInt("num_habitantes"),
                                parroquia_id = resultSet.getInt("parroquia_id")
                            )
                        )
                    }
                    resultSet.close()
                }
            }
        } catch (e: SQLException) {
            Log.e("UbicacionDao", "Error al obtener barrios: ${e.message}")
        }

        return barrios
    }
}