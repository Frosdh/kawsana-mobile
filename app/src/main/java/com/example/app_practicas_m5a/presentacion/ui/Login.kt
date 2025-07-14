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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    private lateinit var etCedula: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIniciar: Button
    private lateinit var btnRegistrar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etCedula = findViewById(R.id.etCedula)
        etPassword = findViewById(R.id.etPassword)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnIniciar.setOnClickListener {
            val cedula = etCedula.text.toString().trim()
            val contraseña = etPassword.text.toString().trim()

            if (cedula.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    CoreUsuarioDao.login(cedula, contraseña)
                }

                if (user != null) {
                    Toast.makeText(this@Login, "Bienvenido ${user.nombres}", Toast.LENGTH_LONG).show()

                    val intent = when (user.tipo_usuario.uppercase()) {
                        "ADMIN", "LIDER" -> Intent(this@Login, Pagina_principal_adm::class.java).apply {
                            putExtra("cedula", user.cedula)
                            putExtra("usuario_id", user.id) // ✅ Agregar usuarioId
                        }
                        "VOLUNTARIO" -> Intent(this@Login, Pagina_principal_vol::class.java).apply {
                            putExtra("cedula", user.cedula)
                            putExtra("usuario_id", user.id) // ✅ Agregar usuarioId
                        }
                        else -> {
                            Toast.makeText(this@Login, "Tipo de usuario desconocido: ${user.tipo_usuario}", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                    }


                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@Login, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, Registro_voluntario::class.java))
        }
    }
}
