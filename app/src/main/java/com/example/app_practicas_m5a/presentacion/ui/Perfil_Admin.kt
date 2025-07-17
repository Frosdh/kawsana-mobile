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
                // Guardar cambios: validar primero
                if (!validarCampos()) {
                    return@setOnClickListener
                }

                usuario?.let {
                    it.nombres = tvNombres.text.toString().trim()
                    it.apellidos = tvApellidos.text.toString().trim()
                    it.email = tvEmail.text.toString().trim()
                    it.telefono = tvTelefono.text.toString().trim()
                    it.direccion = tvDireccion.text.toString().trim()

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

    private fun validarCampos(): Boolean {
        val nombres = tvNombres.text.toString().trim()
        val apellidos = tvApellidos.text.toString().trim()
        val email = tvEmail.text.toString().trim()
        val telefono = tvTelefono.text.toString().trim()
        val direccion = tvDireccion.text.toString().trim()
        val cedula = tvCedula.text.toString().trim()

        if (nombres.isEmpty()) {
            tvNombres.error = "Ingrese nombres"
            tvNombres.requestFocus()
            return false
        }
        if (apellidos.isEmpty()) {
            tvApellidos.error = "Ingrese apellidos"
            tvApellidos.requestFocus()
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmail.error = "Ingrese un email válido"
            tvEmail.requestFocus()
            return false
        }
        if (telefono.isEmpty() || telefono.length < 7) {
            tvTelefono.error = "Ingrese un teléfono válido"
            tvTelefono.requestFocus()
            return false
        }
        if (direccion.isEmpty()) {
            tvDireccion.error = "Ingrese dirección"
            tvDireccion.requestFocus()
            return false
        }
        if (!validarCedulaEcuatoriana(cedula)) {
            tvCedula.error = "Cédula ecuatoriana inválida"
            tvCedula.requestFocus()
            return false
        }
        return true
    }

    // Validación de cédula ecuatoriana
    private fun validarCedulaEcuatoriana(cedula: String): Boolean {
        if (cedula.length != 10) return false
        val region = cedula.substring(0, 2).toIntOrNull() ?: return false
        if (region < 1 || region > 24) return false
        val digitos = cedula.map { it.toString().toIntOrNull() ?: return false }

        val ultimoDigito = digitos[9]
        var sumaPar = 0
        var sumaImpar = 0

        for (i in 0..8 step 2) {
            var valParcial = digitos[i] * 2
            if (valParcial > 9) valParcial -= 9
            sumaImpar += valParcial
        }
        for (i in 1..7 step 2) {
            sumaPar += digitos[i]
        }

        val sumaTotal = sumaImpar + sumaPar
        val decenaSuperior = ((sumaTotal + 9) / 10) * 10
        val digitoVerificador = decenaSuperior - sumaTotal
        val digitoFinal = if (digitoVerificador == 10) 0 else digitoVerificador

        return ultimoDigito == digitoFinal
    }
}
