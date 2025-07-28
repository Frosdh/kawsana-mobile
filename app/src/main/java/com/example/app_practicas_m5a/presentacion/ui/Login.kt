package com.example.app_practicas_m5a.presentacion.ui

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.CoreUsuarioDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIniciar: Button
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val arcView = findViewById<ArcTextImageView>(R.id.arcTextImageView)
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.iconofinalkawsana)
        arcView.setImageBitmap(bmp)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnIniciar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contraseña = etPassword.text.toString().trim()

            // Validaciones
            if (usuario.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuario.length < 4) {
                Toast.makeText(this, "El usuario debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraseña.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val regex = Regex("^[a-zA-Z0-9_@.]*$")
            if (!regex.matches(usuario)) {
                Toast.makeText(this, "El usuario contiene caracteres no permitidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // (Opcional) Validación si el usuario es un correo electrónico
            /*
            if (!Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
                Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            */

            btnIniciar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val user = withContext(Dispatchers.IO) {
                        CoreUsuarioDao.login(usuario, contraseña)
                    }

                    btnIniciar.isEnabled = true

                    if (user != null) {
                        Toast.makeText(this@Login, "Bienvenido ${user.nombres}", Toast.LENGTH_LONG).show()

                        val intent = when (user.tipo_usuario.lowercase()) {
                            "super_admin" -> Intent(this@Login, Pagina_principal_Super_Admin::class.java)
                            "lider" -> Intent(this@Login, Pagina_principal_adm::class.java)
                            "ciudadano" -> Intent(this@Login, Pagina_principal_vol::class.java)
                            else -> {
                                Toast.makeText(this@Login, "Tipo de usuario desconocido", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                        }

                        intent.putExtra("usuario", user.usuario)
                        intent.putExtra("usuario_id", user.id)
                        intent.putExtra("nombre", user.nombres)

                        getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().apply {
                            putLong("usuario_id", user.id)
                            putString("usuario", user.usuario)
                            putString("nombre", user.nombres)
                            putString("tipo_usuario", user.tipo_usuario)
                            apply()
                        }

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    btnIniciar.isEnabled = true
                    Log.e("LoginError", "Error al intentar iniciar sesión", e)
                    Toast.makeText(this@Login, "Error de conexión o del sistema", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, Registro_voluntario::class.java))
        }
    }
}
