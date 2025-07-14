package com.example.app_practicas_m5a.data.dao

import com.example.app_practicas_m5a.data.model.Barrio
import com.example.app_practicas_m5a.data.model.Ciudad
import com.example.app_practicas_m5a.data.model.Parroquia
import java.sql.Connection
import java.sql.DriverManager.getConnection
import java.sql.ResultSet
import java.sql.SQLException

object ubicacion {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    fun obtenerCiudades(): List<Ciudad> {
        val ciudades = mutableListOf<Ciudad>()
        val connection = getConnection()
        val query = "SELECT id, nombre FROM core_ciudad"

        try {
            val statement = connection?.createStatement()
            val resultSet: ResultSet = statement?.executeQuery(query) ?: return ciudades
            while (resultSet.next()) {
                val ciudad = Ciudad(
                    id = resultSet.getInt("id"),
                    nombre = resultSet.getString("nombre")
                )
                ciudades.add(ciudad)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        return ciudades
    }


    fun obtenerParroquiasPorCiudad(ciudadId: Int): List<Parroquia> {
        val parroquias = mutableListOf<Parroquia>()
        val connection = getConnection()
        val query = "SELECT id, nombre FROM core_parroquia WHERE ciudad_id = ?"

        try {
            val preparedStatement = connection?.prepareStatement(query)
            preparedStatement?.setInt(1, ciudadId)
            val resultSet: ResultSet = preparedStatement?.executeQuery() ?: return parroquias
            while (resultSet.next()) {
                val parroquia = Parroquia(
                    id = resultSet.getInt("id"),
                    nombre = resultSet.getString("nombre")
                )
                parroquias.add(parroquia)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        return parroquias
    }


    fun obtenerBarriosPorParroquia(parroquiaId: Int): List<Barrio> {
        val barrios = mutableListOf<Barrio>()
        val connection = getConnection()
        val query = "SELECT id, nombre FROM core_barrio WHERE parroquia_id = ?"

        try {
            val preparedStatement = connection?.prepareStatement(query)
            preparedStatement?.setInt(1, parroquiaId)
            val resultSet: ResultSet = preparedStatement?.executeQuery() ?: return barrios
            while (resultSet.next()) {
                val barrio = Barrio(
                    id = resultSet.getInt("id"),
                    nombre = resultSet.getString("nombre")
                )
                barrios.add(barrio)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        return barrios
    }
}


