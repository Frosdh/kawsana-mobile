package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.ProyectoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Proyecto_Actuales_Lider : AppCompatActivity() {

    private lateinit var layoutProyectos: LinearLayout
    private lateinit var btnSubirActividad: Button

    private var ultimoCheckBoxSeleccionado: CheckBox? = null
    private var proyectoSeleccionadoId: Long? = null

    private var usuarioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyecto_actuales_lider)

        layoutProyectos = findViewById(R.id.layoutProyectos)
        btnSubirActividad = findViewById(R.id.btnSubirActividad)

        val cedula = intent.getStringExtra("cedula") ?: return
        usuarioId = intent.getLongExtra("usuario_id", -1)  // Recibir usuarioId correctamente

        lifecycleScope.launch {
            val proyectos = withContext(Dispatchers.IO) {
                ProyectoDao.obtenerProyectosDelLider(cedula)
            }

            if (proyectos.isEmpty()) {
                val txt = TextView(this@Proyecto_Actuales_Lider).apply {
                    text = "No tienes proyectos actuales asignados"
                }
                layoutProyectos.addView(txt)
            } else {
                proyectos.forEach { proyecto ->
                    val checkBox = CheckBox(this@Proyecto_Actuales_Lider).apply {
                        text = "📌 ${proyecto.nombre}\n📝 ${proyecto.descripcion}\n📅 ${proyecto.fechaInicio} - ${proyecto.fechaFin}\n📊 Progreso: ${proyecto.progreso}"
                        setPadding(0, 20, 0, 20)

                        setOnClickListener {
                            if (ultimoCheckBoxSeleccionado != null && ultimoCheckBoxSeleccionado != this) {
                                ultimoCheckBoxSeleccionado?.isChecked = false
                            }
                            ultimoCheckBoxSeleccionado = this.takeIf { isChecked }
                            proyectoSeleccionadoId = if (isChecked) proyecto.id else null
                        }
                    }
                    layoutProyectos.addView(checkBox)
                }
            }
        }

        btnSubirActividad.setOnClickListener {
            if (proyectoSeleccionadoId == null) {
                Toast.makeText(this, "Selecciona un proyecto para subir actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, Subir_Actividades_Proyecto::class.java)
            intent.putExtra("actividad_id", proyectoSeleccionadoId)  // proyecto id
            intent.putExtra("usuario_id", usuarioId)                // pasar usuarioId también
            startActivity(intent)
        }
    }
}

