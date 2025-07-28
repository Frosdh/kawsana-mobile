package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Perfil_voluntario : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etCedula: EditText
    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var btnModificar: Button
    private lateinit var btnVolver: Button

    private var usuario: CoreUsuarioModel? = null
    private lateinit var nombreUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_voluntario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asignar vistas
        etCedula = findViewById(R.id.etCedulaPerfil)
        etNombres = findViewById(R.id.etNombresPerfil)
        etApellidos = findViewById(R.id.etApellidosPerfil)
        etEmail = findViewById(R.id.etEmailPerfil)
        etTelefono = findViewById(R.id.etTelefonoPerfil)
        etDireccion = findViewById(R.id.etDireccionPerfil)
        btnModificar = findViewById(R.id.btnModificar)
        btnVolver = findViewById(R.id.btnVolver)

        // La cédula nunca se edita
        etCedula.isEnabled = false

        // Recibir nombre de usuario (clave principal)
        nombreUsuario = intent.getStringExtra("usuario") ?: return

        // Cargar datos desde la base
        cargarDatos(nombreUsuario)

        btnModificar.setOnClickListener {
            val estaEditable = etEmail.isEnabled
            if (estaEditable) {
                // VALIDACIONES ANTES DE GUARDAR
                val nombres = etNombres.text.toString().trim()
                val apellidos = etApellidos.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val telefono = etTelefono.text.toString().trim()
                val direccion = etDireccion.text.toString().trim()

                // Validación de nombres
                if (nombres.isEmpty() || !nombres.matches(Regex("^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$"))) {
                    etNombres.error = "Nombres inválidos"
                    etNombres.requestFocus()
                    return@setOnClickListener
                }

                // Validación de apellidos
                if (apellidos.isEmpty() || !apellidos.matches(Regex("^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$"))) {
                    etApellidos.error = "Apellidos inválidos"
                    etApellidos.requestFocus()
                    return@setOnClickListener
                }

                // Validación de email
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.error = "Correo inválido"
                    etEmail.requestFocus()
                    return@setOnClickListener
                }

                // Validación de teléfono
                if (telefono.isEmpty() || !telefono.matches(Regex("^\\d{7,10}$"))) {
                    etTelefono.error = "Teléfono inválido"
                    etTelefono.requestFocus()
                    return@setOnClickListener
                }

                // Validación de dirección
                if (direccion.isEmpty()) {
                    etDireccion.error = "Dirección requerida"
                    etDireccion.requestFocus()
                    return@setOnClickListener
                }

                // SI TODO ESTÁ OK, ACTUALIZAR
                usuario?.let {
                    it.nombres = nombres
                    it.apellidos = apellidos
                    it.email = email
                    it.telefono = telefono
                    it.direccion = direccion

                    lifecycleScope.launch {
                        val actualizado = withContext(Dispatchers.IO) {
                            CoreUsuarioDao.actualizarPerfil(it)
                        }
                        if (actualizado) {
                            Toast.makeText(this@Perfil_voluntario, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            setEditable(false)
                            btnModificar.text = "Modificar"
                        } else {
                            Toast.makeText(this@Perfil_voluntario, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                setEditable(true)
                btnModificar.text = "Guardar cambios"
            }
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, Pagina_principal_vol::class.java)
            intent.putExtra("usuario", nombreUsuario)
            startActivity(intent)
            finish()
        }
    }

    private fun setEditable(habilitar: Boolean) {
        etNombres.isEnabled = habilitar
        etApellidos.isEnabled = habilitar
        etEmail.isEnabled = habilitar
        etTelefono.isEnabled = habilitar
        etDireccion.isEnabled = habilitar
    }

    private fun cargarDatos(nombreUsuario: String) {
        lifecycleScope.launch {
            usuario = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorUsuario(nombreUsuario)
            }

            usuario?.let {
                etCedula.setText(it.cedula)
                etNombres.setText(it.nombres)
                etApellidos.setText(it.apellidos)
                etEmail.setText(it.email)
                etTelefono.setText(it.telefono)
                etDireccion.setText(it.direccion)
            }
        }
    }
}
