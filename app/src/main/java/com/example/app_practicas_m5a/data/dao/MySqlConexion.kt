package com.example.app_practicas_m5a.data.dao

import android.util.Log
import com.example.app_practicas_m5a.data.model.AuthUserModel
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object MySqlConexion {

    // Método para obtener la conexión a la base de datos
    fun getConexion(): Connection? {

        return try {
            // Asegúrate de usar el driver correcto y que esté en tus dependencias
            Class.forName("com.mysql.jdbc.Driver")
            DriverManager.getConnection(
                "jdbc:mysql://10.0.2.2:3306/kawsana_db?useSSL=false&serverTimezone=UTC",
                "Frosdh",
                "blancoss"
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
