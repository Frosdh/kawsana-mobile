package com.example.app_practicas_m5a.data.model

import java.sql.Connection
import java.sql.DriverManager

object MySqlConexion {
    fun getConexion(): Connection? {
        return try {
            // Asegúrate de tener agregado mysql-connector-java:5.1.x
            Class.forName("com.mysql.jdbc.Driver")
            DriverManager.getConnection(
                "jdbc:mysql://10.0.2.2:3306/kawsana_db", // IP correcta para emulador
                "Frosdh",
                "blancoss"
            )
        } catch (e: ClassNotFoundException) {
            println("Error: Driver JDBC no encontrado - ${e.message}")
            null
        } catch (e: Exception) {
            println("Error de conexión a la BD - ${e.message}")
            null
        }
    }
}
