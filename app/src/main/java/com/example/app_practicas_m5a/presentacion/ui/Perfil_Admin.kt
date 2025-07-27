package com.example.app_practicas_m5a.presentacion.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.PerfilCompletoModel.PerfilCompletoModel
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.dao.ubicacion
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import com.example.app_practicas_m5a.data.model.Ciudad
import com.example.app_practicas_m5a.data.model.Parroquia
import com.example.app_practicas_m5a.data.model.Barrio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class Perfil_Admin : AppCompatActivity() {

    private lateinit var tvCedula: EditText
    private lateinit var tvNombres: EditText
    private lateinit var tvApellidos: EditText
    private lateinit var tvEmail: EditText
    private lateinit var tvTelefono: EditText
    private lateinit var tvDireccion: EditText
    private lateinit var btnEditarPerfil: Button
    private lateinit var btnVolver: Button

    private lateinit var spinnerCiudad: Spinner
    private lateinit var spinnerParroquia: Spinner
    private lateinit var spinnerBarrio: Spinner

    private var ciudadSeleccionada: Ciudad? = null
    private var parroquiaSeleccionada: Parroquia? = null
    private var barrioSeleccionado: Barrio? = null

    private var ciudadesList: List<Ciudad> = emptyList()
    private var parroquiasList: List<Parroquia> = emptyList()
    private var barriosList: List<Barrio> = emptyList()

    private var perfil: PerfilCompletoModel? = null
    private var usuarioModel: CoreUsuarioModel? = null

    private lateinit var usuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_admin)

        // Vincular vistas
        tvCedula = findViewById(R.id.tvCedula)
        tvNombres = findViewById(R.id.tvNombres)
        tvApellidos = findViewById(R.id.tvApellidos)
        tvEmail = findViewById(R.id.tvEmail)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvDireccion = findViewById(R.id.tvDireccion)

        spinnerCiudad = findViewById(R.id.spinnerCiudad)
        spinnerParroquia = findViewById(R.id.spinnerParroquia)
        spinnerBarrio = findViewById(R.id.spinnerBarrio)

        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnVolver = findViewById(R.id.btnVolver)

        tvCedula.isEnabled = false // La cédula se muestra, no se edita
        setEditable(false)

        usuario = intent.getStringExtra("usuario") ?: ""
        if (usuario.isEmpty()) {
            Toast.makeText(this, "Error: usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosPorUsuario(usuario)

        // Listeners para los Spinners
        spinnerCiudad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                ciudadSeleccionada = ciudadesList.getOrNull(position)
                ciudadSeleccionada?.let {
                    cargarParroquias(it.id, null, null)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { ciudadSeleccionada = null }
        }

        spinnerParroquia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parroquiaSeleccionada = parroquiasList.getOrNull(position)
                parroquiaSeleccionada?.let {
                    cargarBarrios(it.id, null)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { parroquiaSeleccionada = null }
        }

        spinnerBarrio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                barrioSeleccionado = barriosList.getOrNull(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { barrioSeleccionado = null }
        }

        btnEditarPerfil.setOnClickListener {
            if (tvEmail.isEnabled) {
                if (!validarCampos()) return@setOnClickListener

                usuarioModel?.let {
                    it.nombres = tvNombres.text.toString().trim()
                    it.apellidos = tvApellidos.text.toString().trim()
                    it.email = tvEmail.text.toString().trim()
                    it.telefono = tvTelefono.text.toString().trim()
                    it.direccion = tvDireccion.text.toString().trim()
                    it.barrio_id = barrioSeleccionado?.id?.toLong()

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
                setEditable(true)
                btnEditarPerfil.text = "Guardar cambios"
            }
        }

        btnVolver.setOnClickListener { finish() }
    }

    private fun cargarDatosPorUsuario(usuario: String) {
        lifecycleScope.launch {
            val perfil = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerPerfilCompletoPorUsuario(usuario)
            }

            if (perfil == null) {
                Toast.makeText(this@Perfil_Admin, "Perfil no encontrado", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            usuarioModel = CoreUsuarioModel(
                id = perfil.id,
                nombres = perfil.nombres,
                apellidos = perfil.apellidos,
                cedula = perfil.cedula,
                email = perfil.email,
                telefono = perfil.telefono,
                direccion = perfil.direccion,
                tipo_usuario = perfil.tipo_usuario,
                barrio_id = perfil.barrio_id?.toLong(),
                contraseña = "",
                estado = true,
                fecha_nacimiento = Date(),
                fecha_registro = Date(),
                usuario = perfil.usuario,
                puntos = perfil.puntos ?: 0
            )

            withContext(Dispatchers.Main) {
                tvCedula.setText(perfil.cedula)
                tvNombres.setText(perfil.nombres)
                tvApellidos.setText(perfil.apellidos)
                tvEmail.setText(perfil.email)
                tvTelefono.setText(perfil.telefono)
                tvDireccion.setText(perfil.direccion)

                cargarUbicaciones(perfil.nombreCiudad, perfil.nombreParroquia, perfil.nombreBarrio)
            }
        }
    }

    private fun cargarUbicaciones(ciudadNombre: String?, parroquiaNombre: String?, barrioNombre: String?) {
        lifecycleScope.launch {
            ciudadesList = withContext(Dispatchers.IO) { ubicacion.obtenerCiudades() }

            val adapterCiudad = ArrayAdapter(this@Perfil_Admin, android.R.layout.simple_spinner_item, ciudadesList.map { it.nombre })
            adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCiudad.adapter = adapterCiudad

            ciudadNombre?.let {
                val indexCiudad = ciudadesList.indexOfFirst { it.nombre == ciudadNombre }
                if (indexCiudad != -1) {
                    spinnerCiudad.setSelection(indexCiudad)
                    ciudadSeleccionada = ciudadesList[indexCiudad]
                    cargarParroquias(ciudadSeleccionada!!.id, parroquiaNombre, barrioNombre)
                }
            }
        }
    }

    private fun cargarParroquias(ciudadId: Int, parroquiaNombre: String?, barrioNombre: String?) {
        lifecycleScope.launch {
            parroquiasList = withContext(Dispatchers.IO) { ubicacion.obtenerParroquiasPorCiudad(ciudadId) }

            val adapterParroquia = ArrayAdapter(this@Perfil_Admin, android.R.layout.simple_spinner_item, parroquiasList.map { it.nombre })
            adapterParroquia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerParroquia.adapter = adapterParroquia

            parroquiaNombre?.let {
                val indexParroquia = parroquiasList.indexOfFirst { it.nombre == parroquiaNombre }
                if (indexParroquia != -1) {
                    spinnerParroquia.setSelection(indexParroquia)
                    parroquiaSeleccionada = parroquiasList[indexParroquia]
                    cargarBarrios(parroquiaSeleccionada!!.id, barrioNombre)
                }
            }
        }
    }

    private fun cargarBarrios(parroquiaId: Int, barrioNombre: String?) {
        lifecycleScope.launch {
            barriosList = withContext(Dispatchers.IO) { ubicacion.obtenerBarriosPorParroquia(parroquiaId) }

            val adapterBarrio = ArrayAdapter(this@Perfil_Admin, android.R.layout.simple_spinner_item, barriosList.map { it.nombre })
            adapterBarrio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerBarrio.adapter = adapterBarrio

            barrioNombre?.let {
                val indexBarrio = barriosList.indexOfFirst { it.nombre == barrioNombre }
                if (indexBarrio != -1) {
                    spinnerBarrio.setSelection(indexBarrio)
                    barrioSeleccionado = barriosList[indexBarrio]
                }
            }
        }
    }

    private fun setEditable(habilitar: Boolean) {
        val colorFondo = if (habilitar) Color.WHITE else Color.parseColor("#FFEEEEEE")

        listOf(tvNombres, tvApellidos, tvEmail, tvTelefono, tvDireccion).forEach {
            it.isEnabled = habilitar
            it.setBackgroundColor(colorFondo)
        }

        spinnerCiudad.isEnabled = habilitar
        spinnerParroquia.isEnabled = habilitar
        spinnerBarrio.isEnabled = habilitar
    }

    private fun validarCampos(): Boolean {
        val nombres = tvNombres.text.toString().trim()
        val apellidos = tvApellidos.text.toString().trim()
        val email = tvEmail.text.toString().trim()
        val telefono = tvTelefono.text.toString().trim()
        val direccion = tvDireccion.text.toString().trim()

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
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmail.error = "Correo inválido"
            tvEmail.requestFocus()
            return false
        }
        if (telefono.isEmpty() || telefono.length < 7) {
            tvTelefono.error = "Teléfono inválido"
            tvTelefono.requestFocus()
            return false
        }
        if (direccion.isEmpty()) {
            tvDireccion.error = "Ingrese dirección"
            tvDireccion.requestFocus()
            return false
        }
        if (ciudadSeleccionada == null || parroquiaSeleccionada == null || barrioSeleccionado == null) {
            Toast.makeText(this, "Seleccione ciudad, parroquia y barrio", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
