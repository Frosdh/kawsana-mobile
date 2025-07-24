package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.dao.ubicacion
import com.example.app_practicas_m5a.data.model.Barrio
import com.example.app_practicas_m5a.data.model.Ciudad
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import com.example.app_practicas_m5a.data.model.Parroquia
import kotlinx.coroutines.*
import java.util.*

class Registro_voluntario : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCedula: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etContraseña: EditText
    private lateinit var spinnerTipoUsuario: Spinner
    private lateinit var etPasswordAdmin: EditText
    private lateinit var btnLupa: Button
    private lateinit var spinnerCiudad: Spinner
    private lateinit var spinnerParroquia: Spinner
    private lateinit var spinnerBarrio: Spinner
    private lateinit var btnRegistrar: Button

    private var ciudadSeleccionada: Ciudad? = null
    private var parroquiaSeleccionada: Parroquia? = null
    private var barrioSeleccionado: Barrio? = null

    private val adminPasswordCorrecta = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_voluntario)

        // Inicializar views
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etEmail = findViewById(R.id.etEmail)
        etCedula = findViewById(R.id.etCedula)
        etTelefono = findViewById(R.id.etTelefono)
        etDireccion = findViewById(R.id.etDireccion)
        etContraseña = findViewById(R.id.etContraseña)
        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuario)
        etPasswordAdmin = findViewById(R.id.etPasswordAdmin)
        btnLupa = findViewById(R.id.btnLupa)
        spinnerCiudad = findViewById(R.id.spinnerCiudad)
        spinnerParroquia = findViewById(R.id.spinnerParroquia)
        spinnerBarrio = findViewById(R.id.spinnerBarrio)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        val arcView = findViewById<ArcTextImageView>(R.id.arcTextImageView)
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.iconofinalkawsana)
        arcView.setImageBitmap(bmp)

        // Inicializar spinner de tipo usuario
        val tipos = arrayOf("VOLUNTARIO", "ADMIN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoUsuario.adapter = adapter

        // Al seleccionar tipo usuario
        spinnerTipoUsuario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val tipo = tipos[position]
                if (tipo == "ADMIN") {
                    etPasswordAdmin.visibility = View.VISIBLE
                    btnLupa.visibility = View.VISIBLE
                    spinnerCiudad.visibility = View.GONE
                    spinnerParroquia.visibility = View.GONE
                    spinnerBarrio.visibility = View.GONE
                } else {
                    etPasswordAdmin.visibility = View.GONE
                    btnLupa.visibility = View.GONE
                    spinnerCiudad.visibility = View.GONE
                    spinnerParroquia.visibility = View.GONE
                    spinnerBarrio.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Botón lupa para validar contraseña admin y mostrar ubicaciones
        btnLupa.setOnClickListener {
            val passwordAdmin = etPasswordAdmin.text.toString()
            if (passwordAdmin == adminPasswordCorrecta) {
                spinnerCiudad.visibility = View.VISIBLE
                spinnerParroquia.visibility = View.VISIBLE
                spinnerBarrio.visibility = View.VISIBLE

                cargarUbicaciones()
            } else {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

        // Registrar usuario con validaciones
        btnRegistrar.setOnClickListener {
            if (validarFormulario()) {
                val nombres = etNombres.text.toString().trim()
                val apellidos = etApellidos.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val cedula = etCedula.text.toString().trim()
                val telefono = etTelefono.text.toString().trim()
                val direccion = etDireccion.text.toString().trim()
                val contraseña = etContraseña.text.toString()
                val tipoUsuario = spinnerTipoUsuario.selectedItem.toString()

                val usuario = CoreUsuarioModel(
                    email = email,
                    contraseña = contraseña,
                    tipo_usuario = tipoUsuario,
                    fecha_registro = Date(),
                    estado = true,
                    nombres = nombres,
                    apellidos = apellidos,
                    cedula = cedula,
                    telefono = telefono,
                    direccion = direccion,
                    barrio_id = barrioSeleccionado?.id?.toLong()
                )

                CoroutineScope(Dispatchers.Main).launch {
                    val registrado = withContext(Dispatchers.IO) {
                        CoreUsuarioDao.registrarUsuario(usuario)
                    }
                    if (registrado) {
                        Log.d("Registro", "ID del barrio seleccionado: ${barrioSeleccionado?.id}")
                        Toast.makeText(this@Registro_voluntario, "Usuario registrado con éxito", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@Registro_voluntario, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Registro_voluntario, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun cargarUbicaciones() {
        CoroutineScope(Dispatchers.Main).launch {
            val ciudades = withContext(Dispatchers.IO) {
                ubicacion.obtenerCiudades()
            }
            val adapterCiudad = ArrayAdapter(this@Registro_voluntario, android.R.layout.simple_spinner_item, ciudades.map { it.nombre })
            adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCiudad.adapter = adapterCiudad

            spinnerCiudad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    ciudadSeleccionada = ciudades[position]
                    cargarParroquias(ciudadSeleccionada!!.id)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun cargarParroquias(ciudadId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val parroquias = withContext(Dispatchers.IO) {
                ubicacion.obtenerParroquiasPorCiudad(ciudadId)
            }
            val adapterParroquia = ArrayAdapter(this@Registro_voluntario, android.R.layout.simple_spinner_item, parroquias.map { it.nombre })
            adapterParroquia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerParroquia.adapter = adapterParroquia

            spinnerParroquia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    parroquiaSeleccionada = parroquias[position]
                    cargarBarrios(parroquiaSeleccionada!!.id)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun cargarBarrios(parroquiaId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val barrios = withContext(Dispatchers.IO) {
                ubicacion.obtenerBarriosPorParroquia(parroquiaId)
            }
            val adapterBarrio = ArrayAdapter(this@Registro_voluntario, android.R.layout.simple_spinner_item, barrios.map { it.nombre })
            adapterBarrio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerBarrio.adapter = adapterBarrio

            spinnerBarrio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    barrioSeleccionado = barrios[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    // --- VALIDACIONES ---

    private fun validarFormulario(): Boolean {
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val cedula = etCedula.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val contraseña = etContraseña.text.toString()
        val tipoUsuario = spinnerTipoUsuario.selectedItem.toString()

        if (nombres.isEmpty()) {
            etNombres.error = "Ingrese sus nombres"
            etNombres.requestFocus()
            return false
        }
        if (!nombres.matches(Regex("^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$"))) {
            etNombres.error = "Nombres inválidos (solo letras y espacios)"
            etNombres.requestFocus()
            return false
        }

        if (apellidos.isEmpty()) {
            etApellidos.error = "Ingrese sus apellidos"
            etApellidos.requestFocus()
            return false
        }
        if (!apellidos.matches(Regex("^[A-Za-záéíóúÁÉÍÓÚñÑ ]+$"))) {
            etApellidos.error = "Apellidos inválidos (solo letras y espacios)"
            etApellidos.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = "Ingrese su correo electrónico"
            etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Correo electrónico inválido"
            etEmail.requestFocus()
            return false
        }

        if (cedula.isEmpty()) {
            etCedula.error = "Ingrese su cédula"
            etCedula.requestFocus()
            return false
        }
        if (!validarCedulaEcuatoriana(cedula)) {
            etCedula.error = "Cédula ecuatoriana inválida"
            etCedula.requestFocus()
            return false
        }

        if (telefono.isEmpty()) {
            etTelefono.error = "Ingrese su teléfono"
            etTelefono.requestFocus()
            return false
        }
        if (!telefono.matches(Regex("^\\d{7,10}\$"))) {
            etTelefono.error = "Teléfono inválido (7 a 10 dígitos numéricos)"
            etTelefono.requestFocus()
            return false
        }

        if (direccion.isEmpty()) {
            etDireccion.error = "Ingrese su dirección"
            etDireccion.requestFocus()
            return false
        }

        if (contraseña.isEmpty()) {
            etContraseña.error = "Ingrese su contraseña"
            etContraseña.requestFocus()
            return false
        }
        if (!validarContraseña(contraseña)) {
            etContraseña.error = "Contraseña débil (mínimo 8 caracteres, con letras y números)"
            etContraseña.requestFocus()
            return false
        }

        // Si es ADMIN, verificar que contraseña admin sea correcta y que haya seleccionado ubicación
        if (tipoUsuario == "ADMIN") {
            val passAdmin = etPasswordAdmin.text.toString()
            if (passAdmin != adminPasswordCorrecta) {
                Toast.makeText(this, "Contraseña de administrador incorrecta", Toast.LENGTH_SHORT).show()
                return false
            }
            if (ciudadSeleccionada == null || parroquiaSeleccionada == null || barrioSeleccionado == null) {
                Toast.makeText(this, "Debe seleccionar Ciudad, Parroquia y Barrio", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }

    // Validación de cédula ecuatoriana
    private fun validarCedulaEcuatoriana(cedula: String): Boolean {
        if (cedula.length != 10 || !cedula.all { it.isDigit() }) return false

        val digitos = cedula.map { it.toString().toInt() }
        val provincia = digitos[0] * 10 + digitos[1]
        if (provincia < 1 || provincia > 24) return false

        val tercerDigito = digitos[2]
        if (tercerDigito >= 6) return false

        val coeficientes = listOf(2,1,2,1,2,1,2,1,2)
        var suma = 0

        for (i in 0 until 9) {
            var valCoef = digitos[i] * coeficientes[i]
            if (valCoef >= 10) valCoef -= 9
            suma += valCoef
        }

        val modulo = suma % 10
        val digitoVerificador = if (modulo == 0) 0 else 10 - modulo

        return digitoVerificador == digitos[9]
    }

    // Validación simple de contraseña
    private fun validarContraseña(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        return regex.matches(password)
    }
}
