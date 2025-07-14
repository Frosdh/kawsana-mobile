package com.example.app_practicas_m5a.data.dao

import com.example.app_practicas_m5a.presentacion.ui.Proyectos_Disponibles

object ProyectoDisponobleDao {

    fun obtenerTodosLosProyectos(): List<Proyectos_Disponibles> {
        val proyectos = mutableListOf<Proyectos_Disponibles>()
        val conexion = MySqlConexion.getConexion()
        val query = "SELECT nombre, descripcion FROM proyecto"

        val statement = conexion?.prepareStatement(query)
        val resultado = statement?.executeQuery()

        while (resultado?.next() == true) {
            val nombre = resultado.getString("nombre")
            val descripcion = resultado.getString("descripcion")
            //proyectos.add(Proyectos_Disponibles(nombre, descripcion))
        }

        conexion?.close()
        return proyectos
    }
}
