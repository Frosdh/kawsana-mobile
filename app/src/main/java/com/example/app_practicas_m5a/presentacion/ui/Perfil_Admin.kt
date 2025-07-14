package com.example.app_practicas_m5a.presentacion.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Perfil_Admin : AppCompatActivity() {

    private lateinit var tvCedula: EditText
    private lateinit var tvNombres: EditText
    private lateinit var tvApellidos: EditText
    private lateinit var tvEmail: EditText
    private lateinit var tvTelefono: EditText
    private lateinit var tvDireccion: EditText
    private lateinit var btnEditarPerfil: Button
    private lateinit var btnVolver: Button

    private var usuario: CoreUsuarioModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_admin)

        tvCedula = findViewById(R.id.tvCedula)
        tvNombres = findViewById(R.id.tvNombres)
        tvApellidos = findViewById(R.id.tvApellidos)
        tvEmail = findViewById(R.id.tvEmail)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvDireccion = findViewById(R.id.tvDireccion)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnVolver = findViewById(R.id.btnVolver)

        // La cédula nunca se edita
        tvCedula.isEnabled = false

        // Modo inicial: campos deshabilitados, fondo gris (modo solo lectura)
        setEditable(false)

        val cedula = intent.getStringExtra("cedula") ?: return
        cargarDatos(cedula)

        btnEditarPerfil.setOnClickListener {
            if (tvEmail.isEnabled) {
                // Guardar cambios
                usuario?.let {
                    it.nombres = tvNombres.text.toString()
                    it.apellidos = tvApellidos.text.toString()
                    it.email = tvEmail.text.toString()
                    it.telefono = tvTelefono.text.toString()
                    it.direccion = tvDireccion.text.toString()

                    lifecycleScope.launch {
                        val actualizado = withContext(Dispatchers.IO) {
                            CoreUsuarioDao.actualizarPerfil(it)
                        }
                        if (actualizado) {
                            Toast.makeText(this@Perfil_Admin, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            setEditable(false)
                            btnEditarPerfil.text = "Editar Perfil"
                        } else {
                            Toast.makeText(this@Perfil_Admin, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // Habilitar edición
                setEditable(true)
                btnEditarPerfil.text = "Guardar cambios"
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun setEditable(habilitar: Boolean) {
        val colorFondo = if (habilitar) Color.WHITE else Color.parseColor("#FFEEEEEE")

        tvNombres.isEnabled = habilitar
        tvNombres.setBackgroundColor(colorFondo)

        tvApellidos.isEnabled = habilitar
        tvApellidos.setBackgroundColor(colorFondo)

        tvEmail.isEnabled = habilitar
        tvEmail.setBackgroundColor(colorFondo)

        tvTelefono.isEnabled = habilitar
        tvTelefono.setBackgroundColor(colorFondo)

        tvDireccion.isEnabled = habilitar
        tvDireccion.setBackgroundColor(colorFondo)
    }

    private fun cargarDatos(cedula: String) {
        lifecycleScope.launch {
            usuario = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorCedula(cedula)
            }
            usuario?.let {
                tvCedula.setText(it.cedula)
                tvNombres.setText(it.nombres)
                tvApellidos.setText(it.apellidos)
                tvEmail.setText(it.email)
                tvTelefono.setText(it.telefono)
                tvDireccion.setText(it.direccion)
            }
        }
    }
}
