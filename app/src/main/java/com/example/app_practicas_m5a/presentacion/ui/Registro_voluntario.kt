package com.example.app_practicas_m5a.presentacion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import com.example.app_practicas_m5a.data.model.CoreUsuarioModel
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
    private lateinit var btnRegistrar: Button

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
        btnRegistrar = findViewById(R.id.btnRegistrar)

        // Spinner con opciones
        val tipos = arrayOf("VOLUNTARIO", "ADMIN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoUsuario.adapter = adapter

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
                contraseña = contraseña, // ideal hashear antes de guardar
                tipo_usuario = tipoUsuario,
                fecha_registro = Date(),
                estado = true,
                nombres = nombres,
                apellidos = apellidos,
                cedula = cedula,
                telefono = telefono,
                direccion = direccion
            )

            GlobalScope.launch(Dispatchers.Main) {
                val registrado = withContext(Dispatchers.IO) {
                    CoreUsuarioDao.registrarUsuario(usuario)
                }
                if (registrado) {
                    Toast.makeText(this@Registro_voluntario, "Usuario registrado con éxito", Toast.LENGTH_LONG).show()
                    // Volver al login
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
