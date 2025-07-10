package com.example.app_practicas_m5a.data.dao

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

object ubicacion {

    private fun getConnection(): Connection? = MySqlConexion.getConexion()

    fun obtenerCiudades(): List<String> {
        val ciudades = mutableListOf<String>()
        val connection = getConnection()
        val query = "SELECT nombre FROM core_ciudad"
        val statement = connection?.createStatement()

        try {
            val resultSet: ResultSet = statement?.executeQuery(query) ?: return ciudades
            while (resultSet.next()) {
                ciudades.add(resultSet.getString("nombre"))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return ciudades
    }

    fun obtenerParroquiasPorCiudad(ciudadId: Int): List<String> {
        val parroquias = mutableListOf<String>()
        val connection = getConnection()
        val query = "SELECT nombre FROM core_parroquia WHERE ciudad_id = ?"
        val preparedStatement = connection?.prepareStatement(query)

        try {
            preparedStatement?.setInt(1, ciudadId)
            val resultSet: ResultSet = preparedStatement?.executeQuery() ?: return parroquias
            while (resultSet.next()) {
                parroquias.add(resultSet.getString("nombre"))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return parroquias
    }

    fun obtenerBarriosPorParroquia(parroquiaId: Int): List<String> {
        val barrios = mutableListOf<String>()
        val connection = getConnection()
        val query = "SELECT nombre FROM core_barrio WHERE parroquia_id = ?"
        val preparedStatement = connection?.prepareStatement(query)

        try {
            preparedStatement?.setInt(1, parroquiaId)
            val resultSet: ResultSet = preparedStatement?.executeQuery() ?: return barrios
            while (resultSet.next()) {
                barrios.add(resultSet.getString("nombre"))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return barrios
    }
}

