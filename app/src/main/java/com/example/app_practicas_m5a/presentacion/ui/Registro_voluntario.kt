package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.dao.ubicacion
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
import com.example.app_practicas_m5a.data.model.Ciudad
import com.example.app_practicas_m5a.data.model.Parroquia
import com.example.app_practicas_m5a.data.model.Barrio
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_voluntario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        val tipos = arrayOf("VOLUNTARIO", "ADMIN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoUsuario.adapter = adapter

        spinnerTipoUsuario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (spinnerTipoUsuario.selectedItem == "ADMIN") {
                    etPasswordAdmin.visibility = View.VISIBLE
                    btnLupa.visibility = View.VISIBLE
                } else {
                    etPasswordAdmin.visibility = View.GONE
                    btnLupa.visibility = View.GONE
                    spinnerCiudad.visibility = View.GONE
                    spinnerParroquia.visibility = View.GONE
                    spinnerBarrio.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        btnLupa.setOnClickListener {
            val passwordAdmin = etPasswordAdmin.text.toString()
            if (passwordAdmin == "admin123") {
                spinnerCiudad.visibility = View.VISIBLE
                spinnerParroquia.visibility = View.VISIBLE
                spinnerBarrio.visibility = View.VISIBLE

                GlobalScope.launch(Dispatchers.Main) {
                    val ciudades = withContext(Dispatchers.IO) {
                        ubicacion.obtenerCiudades()
                    }
                    val adapterCiudad = ArrayAdapter(this@Registro_voluntario, android.R.layout.simple_spinner_item, ciudades.map { it.nombre })
                    adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCiudad.adapter = adapterCiudad

                    spinnerCiudad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            ciudadSeleccionada = ciudades[position]
                            GlobalScope.launch(Dispatchers.Main) {
                                val parroquias = withContext(Dispatchers.IO) {
                                    ubicacion.obtenerParroquiasPorCiudad(ciudadSeleccionada!!.id)
                                }
                                val adapterParroquia = ArrayAdapter(this@Registro_voluntario, android.R.layout.simple_spinner_item, parroquias.map { it.nombre })
                                adapterParroquia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerParroquia.adapter = adapterParroquia

                                spinnerParroquia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        parroquiaSeleccionada = parroquias[position]
                                        GlobalScope.launch(Dispatchers.Main) {
                                            val barrios = withContext(Dispatchers.IO) {
                                                ubicacion.obtenerBarriosPorParroquia(parroquiaSeleccionada!!.id)
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
                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            } else {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegistrar.setOnClickListener {
            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val cedula = etCedula.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val contraseña = etContraseña.text.toString()
            val tipoUsuario = spinnerTipoUsuario.selectedItem.toString()

            if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || cedula.isEmpty() ||
                telefono.isEmpty() || direccion.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

            GlobalScope.launch(Dispatchers.Main) {
                val registrado = withContext(Dispatchers.IO) {
                    CoreUsuarioDao.registrarUsuario(usuario)
                }
                if (registrado) {
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
