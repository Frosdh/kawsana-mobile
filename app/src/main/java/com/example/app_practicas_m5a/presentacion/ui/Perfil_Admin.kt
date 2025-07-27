package com.example.app_practicas_m5a.presentacion.ui

import PerfilCompletoModel
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.dao.ubicacion
import com.example.app_practicas_m5a.data.model.*
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

    private lateinit var spinnerCiudad: Spinner
    private lateinit var spinnerParroquia: Spinner
    private lateinit var spinnerBarrio: Spinner

    private var ciudadSeleccionada: Ciudad? = null
    private var parroquiaSeleccionada: Parroquia? = null
    private var barrioSeleccionado: Barrio? = null

    private var perfil: PerfilCompletoModel? = null
    private var usuario: CoreUsuarioModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_admin)
        Log.d("Perfil_Admin", "Pantalla cargada correctamente")

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

        tvCedula.isEnabled = false
        setEditable(false)

        val username = intent.getStringExtra("usuario")
        Log.d("Perfil_Admin", "Usuario recibido por intent: $username")

        if (username == null) {
            Log.e("Perfil_Admin", "No se recibió el nombre de usuario por intent")
            return
        }

        cargarDatos(username)

        btnEditarPerfil.setOnClickListener {
            if (tvEmail.isEnabled) {
                if (!validarCampos()) return@setOnClickListener

                usuario?.let {
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
                            Log.d("Perfil_Admin", "Perfil actualizado correctamente")
                            Toast.makeText(this@Perfil_Admin, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            setEditable(false)
                            btnEditarPerfil.text = "Editar Perfil"
                        } else {
                            Log.e("Perfil_Admin", "Error al actualizar el perfil")
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

    private fun cargarDatos(usuarioStr: String) {
        Log.d("Perfil_Admin", "Cargando datos para: $usuarioStr")

        lifecycleScope.launch {
            perfil = withContext(Dispatchers.IO) {
                Log.d("Perfil_Admin", "Consultando DAO para perfil completo por usuario...")
                CoreUsuarioDao.obtenerPerfilCompletoPorUsuario(usuarioStr)
            }

            if (perfil == null) {
                Log.e("Perfil_Admin", "Perfil no encontrado para: $usuarioStr")
                Toast.makeText(this@Perfil_Admin, "No se encontró el perfil", Toast.LENGTH_SHORT).show()
                return@launch
            }

            Log.d("Perfil_Admin", "Perfil cargado: ${perfil!!.nombres} ${perfil!!.apellidos}")

            usuario = CoreUsuarioModel(
                id = perfil!!.id,
                nombres = perfil!!.nombres,
                apellidos = perfil!!.apellidos,
                cedula = perfil!!.cedula,
                email = perfil!!.email,
                telefono = perfil!!.telefono,
                direccion = perfil!!.direccion,
                tipo_usuario = perfil!!.tipo_usuario,
                barrio_id = null,
                contraseña = "",
                estado = true,
                fecha_nacimiento = null,
                fecha_registro = null,
                usuario = perfil!!.usuario
            )

            tvCedula.setText(perfil!!.cedula)
            tvNombres.setText(perfil!!.nombres)
            tvApellidos.setText(perfil!!.apellidos)
            tvEmail.setText(perfil!!.email)
            tvTelefono.setText(perfil!!.telefono)
            tvDireccion.setText(perfil!!.direccion)

            cargarUbicaciones(perfil!!.nombreCiudad, perfil!!.nombreParroquia, perfil!!.nombreBarrio)
        }
    }

    private fun cargarUbicaciones(ciudadNombre: String, parroquiaNombre: String, barrioNombre: String) {
        Log.d("Perfil_Admin", "Cargando ubicaciones: $ciudadNombre, $parroquiaNombre, $barrioNombre")

        lifecycleScope.launch {
            val ciudades = withContext(Dispatchers.IO) { ubicacion.obtenerCiudades() }
            Log.d("Perfil_Admin", "Ciudades obtenidas: ${ciudades.size}")

            val adapterCiudad = ArrayAdapter(this@Perfil_Admin, android.R.layout.simple_spinner_item, ciudades.map { it.nombre })
            adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCiudad.adapter = adapterCiudad

            val indexCiudad = ciudades.indexOfFirst { it.nombre == ciudadNombre }
            if (indexCiudad != -1) {
                spinnerCiudad.setSelection(indexCiudad)
                ciudadSeleccionada = ciudades[indexCiudad]
                cargarParroquias(ciudadSeleccionada!!.id, parroquiaNombre, barrioNombre)
            } else {
                Log.e("Perfil_Admin", "Ciudad '$ciudadNombre' no encontrada en la lista")
            }
        }
    }

    private fun cargarParroquias(ciudadId: Int, parroquiaNombre: String, barrioNombre: String) {
        Log.d("Perfil_Admin", "Cargando parroquias para ciudad ID: $ciudadId")

        lifecycleScope.launch {
            val parroquias = withContext(Dispatchers.IO) { ubicacion.obtenerParroquiasPorCiudad(ciudadId) }
            Log.d("Perfil_Admin", "Parroquias obtenidas: ${parroquias.size}")

            val adapterParroquia = ArrayAdapter(this@Perfil_Admin, android.R.layout.simple_spinner_item, parroquias.map { it.nombre })
            adapterParroquia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerParroquia.adapter = adapterParroquia

            val indexParroquia = parroquias.indexOfFirst { it.nombre == parroquiaNombre }
            if (indexParroquia != -1) {
                spinnerParroquia.setSelection(indexParroquia)
                parroquiaSeleccionada = parroquias[indexParroquia]
                cargarBarrios(parroquiaSeleccionada!!.id, barrioNombre)
            } else {
                Log.e("Perfil_Admin", "Parroquia '$parroquiaNombre' no encontrada")
            }
        }
    }

    private fun cargarBarrios(parroquiaId: Int, barrioNombre: String) {
        Log.d("Perfil_Admin", "Cargando barrios para parroquia ID: $parroquiaId")

        lifecycleScope.launch {
            val barrios = withContext(Dispatchers.IO) { ubicacion.obtenerBarriosPorParroquia(parroquiaId) }
            Log.d("Perfil_Admin", "Barrios obtenidos: ${barrios.size}")

            val adapterBarrio = ArrayAdapter(this@Perfil_Admin, android.R.layout.simple_spinner_item, barrios.map { it.nombre })
            adapterBarrio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerBarrio.adapter = adapterBarrio

            val indexBarrio = barrios.indexOfFirst { it.nombre == barrioNombre }
            if (indexBarrio != -1) {
                spinnerBarrio.setSelection(indexBarrio)
                barrioSeleccionado = barrios[indexBarrio]
                Log.d("Perfil_Admin", "Barrio seleccionado: $barrioNombre")
            } else {
                Log.e("Perfil_Admin", "Barrio '$barrioNombre' no encontrado")
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
        val cedula = tvCedula.text.toString().trim()

        if (nombres.isEmpty()) {
            tvNombres.error = "Ingrese nombres"
            tvNombres.requestFocus(); return false
        }
        if (apellidos.isEmpty()) {
            tvApellidos.error = "Ingrese apellidos"
            tvApellidos.requestFocus(); return false
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmail.error = "Correo inválido"
            tvEmail.requestFocus(); return false
        }
        if (telefono.isEmpty() || telefono.length < 7) {
            tvTelefono.error = "Teléfono inválido"
            tvTelefono.requestFocus(); return false
        }
        if (direccion.isEmpty()) {
            tvDireccion.error = "Ingrese dirección"
            tvDireccion.requestFocus(); return false
        }
        if (!validarCedulaEcuatoriana(cedula)) {
            tvCedula.error = "Cédula ecuatoriana inválida"
            tvCedula.requestFocus(); return false
        }
        if (ciudadSeleccionada == null || parroquiaSeleccionada == null || barrioSeleccionado == null) {
            Toast.makeText(this, "Seleccione ciudad, parroquia y barrio", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

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
