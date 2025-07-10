package com.example.app_practicas_m5a.presentacion.ui


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

class Perfil_Admin : AppCompatActivity()  {

    private lateinit var etEmail: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etCedula: EditText
    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var btnModificar: Button
    private lateinit var btnVolver: Button

    private var usuario: CoreUsuarioModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asignar vistas
        etCedula = findViewById(R.id.tvCedula)
        etNombres = findViewById(R.id.tvNombres)
        etApellidos = findViewById(R.id.tvApellidos)
        etEmail = findViewById(R.id.tvEmail)
        etTelefono = findViewById(R.id.tvTelefono)
        etDireccion = findViewById(R.id.tvDireccion)
        btnModificar = findViewById(R.id.btnEditarPerfil)
        btnVolver = findViewById(R.id.btnVolver)

        // La cédula nunca se edita
        etCedula.isEnabled = false

        // Recibir cédula
        val cedula = intent.getStringExtra("cedula_usuario") ?: return

        // Cargar datos
        cargarDatos(cedula)

        btnModificar.setOnClickListener {
            val estaEditable = etEmail.isEnabled
            if (estaEditable) {
                usuario?.let {
                    it.nombres = etNombres.text.toString()
                    it.apellidos = etApellidos.text.toString()
                    it.email = etEmail.text.toString()
                    it.telefono = etTelefono.text.toString()
                    it.direccion = etDireccion.text.toString()

                    lifecycleScope.launch {
                        val actualizado = withContext(Dispatchers.IO) {
                            CoreUsuarioDao.actualizarPerfil(it)
                        }
                        if (actualizado) {
                            Toast.makeText(this@Perfil_Admin, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            setEditable(false)
                            btnModificar.text = "Modificar"
                        } else {
                            Toast.makeText(this@Perfil_Admin, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                setEditable(true)
                btnModificar.text = "Guardar cambios"
            }
        }

        btnVolver.setOnClickListener {
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

    private fun cargarDatos(cedula: String) {
        lifecycleScope.launch {
            usuario = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorCedula(cedula)
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