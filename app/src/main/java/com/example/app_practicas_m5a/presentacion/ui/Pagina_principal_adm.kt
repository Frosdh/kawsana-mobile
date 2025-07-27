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
import com.example.app_practicas_m5a.data.dao.ProyectoDisponobleDao
import com.example.app_practicas_m5a.data.model.Proyectos
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class Pagina_principal_adm : AppCompatActivity() {

    private lateinit var tvTituloAdmin: TextView
    private lateinit var tvNombreAdmin: TextView
    private lateinit var tvCorreoAdmin: TextView
    private lateinit var imgPerfil: ImageView
    private lateinit var btnVerPerfil: Button
    private lateinit var btnFinal: Button
    private lateinit var tvProgresoProyecto: TextView
    private lateinit var barChartProgreso: BarChart

    private lateinit var cedula: String
    private var usuarioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal_adm)

        // Vincular UI
        tvTituloAdmin = findViewById(R.id.tvTituloAdmin)
        tvNombreAdmin = findViewById(R.id.tvNombreAdmin)
        tvCorreoAdmin = findViewById(R.id.tvCorreoAdmin)
        imgPerfil = findViewById(R.id.imgPerfil)
        btnVerPerfil = findViewById(R.id.btnVerPerfil)
        btnFinal = findViewById(R.id.btnFinal)
        tvProgresoProyecto = findViewById(R.id.tvProgresoProyecto)
        barChartProgreso = findViewById(R.id.barChartProgreso)


        cedula = intent.getStringExtra("cedula") ?: ""

        lifecycleScope.launch {
            val admin = withContext(Dispatchers.IO) {
                CoreUsuarioDao.obtenerUsuarioPorCedula(cedula)
            }

            if (admin != null) {
                usuarioId = admin.id
                tvNombreAdmin.text = "Nombre: ${admin.nombres} ${admin.apellidos}"
                tvCorreoAdmin.text = "Correo: ${admin.email}"
            } else {
                Toast.makeText(this@Pagina_principal_adm, "No se encontró el administrador", Toast.LENGTH_SHORT).show()
            }
        }

        cargarProyectoConProgreso()

        btnVerPerfil.setOnClickListener {
            val intent = Intent(this, Perfil_Admin::class.java)
            intent.putExtra("cedula", cedula)
            startActivity(intent)
        }

        val btnVerProyecto = findViewById<Button>(R.id.btnVerProyecto)
        btnVerProyecto.setOnClickListener {
            val intent = Intent(this, Proyectos_Disponibles::class.java)
            intent.putExtra("titulo", "Proyecto Kausana")
            intent.putExtra("descripcion", "Plataforma de apoyo social para comunidades rurales.")
            startActivity(intent)
        }

        btnFinal.setOnClickListener {
            val intent = Intent(this, Proyecto_Actuales_Lider::class.java)
            intent.putExtra("cedula", cedula)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInicioAdmin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnVoluntariosBarrios = findViewById<Button>(R.id.btnVerVoluntarios)

        btnVoluntariosBarrios.setOnClickListener {
            val intent = Intent(this, VoluntariosBarrio::class.java)
            startActivity(intent)
        }

    }

    private fun cargarProyectoConProgreso() {
        lifecycleScope.launch {
            val proyectos = withContext(Dispatchers.IO) {
                ProyectoDisponobleDao.obtenerTodosLosProyectosConProgreso()
            }

            if (proyectos.isNotEmpty()) {
                // Mostrar progreso del primer proyecto en TextView
                val primerProyecto = proyectos[0]
                tvProgresoProyecto.text = "Progreso: ${primerProyecto.progreso}%"

                // Mostrar gráfico de barras con todos los proyectos
                mostrarGraficoBarras(proyectos)
            } else {
                tvProgresoProyecto.text = "No hay proyectos disponibles"
            }
        }
    }

    private fun mostrarGraficoBarras(proyectos: List<Proyectos>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        proyectos.forEachIndexed { index, proyecto ->
            entries.add(BarEntry(index.toFloat(), proyecto.progreso.toFloat()))
            labels.add(proyecto.nombre)
        }

        val dataSet = BarDataSet(entries, "Progreso de proyectos (%)")
        dataSet.color = resources.getColor(R.color.teal_700, null)

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChartProgreso.data = data
        barChartProgreso.setFitBars(true)
        barChartProgreso.description.isEnabled = false

        val xAxis = barChartProgreso.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChartProgreso.axisLeft.axisMinimum = 0f
        barChartProgreso.axisLeft.axisMaximum = 100f
        barChartProgreso.axisRight.isEnabled = false

        barChartProgreso.invalidate()
    }
}