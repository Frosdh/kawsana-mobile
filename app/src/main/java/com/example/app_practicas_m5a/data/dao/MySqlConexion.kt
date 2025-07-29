package com.example.app_practicas_m5a.data.dao

import android.util.Log
import java.sql.Connection
import java.sql.DriverManager

object MySqlConexion {

    fun getConexion(): Connection? {
        return try {
            // Carga del driver JDBC
            Class.forName("com.mysql.jdbc.Driver")

            // Conexión a la base de datos en la nube
            //
            DriverManager.getConnection(
                "jdbc:mysql://kawsana.ccz2262m6wg3.us-east-1.rds.amazonaws.com:3306/kawsana_db?useSSL=false&serverTimezone=UTC",
                "admin", // Usuario de la base de datos en la nube
                "kawsana1234" // Contraseña de la base de datos en la nube
            )
        } catch (e: ClassNotFoundException) {
            Log.e("MySqlConexion", "Error: Driver JDBC no encontrado - ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("MySqlConexion", "Error de conexión a la BD - ${e.message}")
            null
        }
    }
}
