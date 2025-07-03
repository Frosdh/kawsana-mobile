package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.MySqlConexion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var tvEstado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvEstado = findViewById(R.id.tvEstadoConexion)
        tvEstado.text = "Conectando a la base de datos..."

        CoroutineScope(Dispatchers.IO).launch {
            val conexion = MySqlConexion.getConexion()
            val mensaje: String
            val exito: Boolean

            if (conexion != null) {
                conexion.close()
                mensaje = "✅ ¡Conexión exitosa!"
                exito = true
            } else {
                mensaje = "❌ Error al conectar con la base de datos"
                exito = false
            }

            withContext(Dispatchers.Main) {
                tvEstado.text = mensaje
                Toast.makeText(this@MainActivity, mensaje, Toast.LENGTH_LONG).show()


            }
        }
    }
}