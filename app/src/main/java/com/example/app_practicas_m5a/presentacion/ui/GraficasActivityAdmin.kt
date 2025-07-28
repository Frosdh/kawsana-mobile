package com.example.app_practicas_m5a.presentacion.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.BarrioDaoProgresoGrafico
import com.example.app_practicas_m5a.data.model.ActividadResumen
import com.example.app_practicas_m5a.data.model.BarrioProgreso
import com.example.app_practicas_m5a.data.model.BarrioUsuariosLideres
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class GraficasActivityAdmin : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var pieChartBarrios: PieChart
    private lateinit var pieChartActividades: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficas_admin)

        barChart = findViewById(R.id.barChart)
        pieChartBarrios = findViewById(R.id.pieChartBarrios)
        pieChartActividades = findViewById(R.id.pieChartActividades)

        lifecycleScope.launch {
            // Gráfica de barras - Top 6 barrios
            val datosProgreso = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerTop6Barrios()
            }
            if (datosProgreso.isNotEmpty()) {
                mostrarGraficaBarras(datosProgreso)
            }

            // Gráfica de pastel - Usuarios y líderes por barrio
            val datosUsuariosYLideres = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerUsuariosYLideresPorBarrio()
            }
            if (datosUsuariosYLideres.isNotEmpty()) {
                mostrarGraficaPastelBarrios(datosUsuariosYLideres)
            } else {
                pieChartBarrios.clear()
                pieChartBarrios.setNoDataText("No hay datos para mostrar")
                pieChartBarrios.invalidate()
            }

            // Gráfica de pastel - Actividades realizadas / no realizadas
            val resumenActividades = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerResumenActividades()
            }
            mostrarGraficaPastelActividades(resumenActividades)
        }
    }

    private fun mostrarGraficaBarras(datos: List<BarrioProgreso>) {
        val datosFiltrados = datos.filter { it.progreso > 0f }
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for ((index, dato) in datosFiltrados.withIndex()) {
            entries.add(BarEntry(index.toFloat(), dato.progreso))
            labels.add(dato.nombreBarrio)
        }

        val dataSet = BarDataSet(entries, "Top 6 Barrios por Progreso (%)")

        val random = Random()
        val colores = List(entries.size) {
            Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
        }
        dataSet.colors = colores

        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -30f

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description = Description().apply { text = "" }

        barChart.animateY(1500)
        barChart.invalidate()
    }

    private fun mostrarGraficaPastelBarrios(datos: List<BarrioUsuariosLideres>) {
        val datosFiltrados = datos.filter { (it.totalUsuarios + it.totalLideres) > 0 }
        if (datosFiltrados.isEmpty()) {
            pieChartBarrios.clear()
            pieChartBarrios.setNoDataText("No hay datos para mostrar")
            pieChartBarrios.invalidate()
            return
        }

        val totalGeneral = datosFiltrados.sumOf { it.totalUsuarios + it.totalLideres }.toFloat()
        val entries = datosFiltrados.map { dato ->
            val cantidad = (dato.totalUsuarios + dato.totalLideres).toFloat()
            val porcentaje = (cantidad / totalGeneral) * 100f
            PieEntry(porcentaje, dato.nombreBarrio ?: "Sin nombre")
        }

        val dataSet = PieDataSet(entries, "Distribución por barrio")

        val colores = mutableListOf<Int>().apply {
            addAll(ColorTemplate.MATERIAL_COLORS.toList())
            addAll(ColorTemplate.COLORFUL_COLORS.toList())
            addAll(ColorTemplate.VORDIPLOM_COLORS.toList())
            addAll(ColorTemplate.PASTEL_COLORS.toList())
            addAll(ColorTemplate.LIBERTY_COLORS.toList())

            repeat(20) {
                add(Color.rgb((50..255).random(), (50..255).random(), (50..255).random()))
            }

            shuffle()
        }

        dataSet.colors = colores
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)
        pieChartBarrios.setUsePercentValues(true)
        pieChartBarrios.data = pieData
        pieChartBarrios.description.isEnabled = false
        pieChartBarrios.setEntryLabelColor(Color.BLACK)
        pieChartBarrios.centerText = "Distribución"
        pieChartBarrios.animateY(1000)
        pieChartBarrios.invalidate()
    }

    private fun mostrarGraficaPastelActividades(resumen: ActividadResumen) {
        val total = resumen.realizadas + resumen.noRealizadas
        if (total == 0) {
            pieChartActividades.clear()
            pieChartActividades.setNoDataText("No hay actividades registradas")
            pieChartActividades.invalidate()
            return
        }

        val entries = listOf(
            PieEntry(resumen.realizadas.toFloat(), "Realizadas"),
            PieEntry(resumen.noRealizadas.toFloat(), "No realizadas")
        )

        val dataSet = PieDataSet(entries, "Estado de Actividades")

        // Generar colores aleatorios para las dos porciones
        val random = Random()
        val colores = listOf(
            Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)),
            Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
        )
        dataSet.colors = colores

        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)

        pieChartBarrios.setUsePercentValues(true)
        pieChartActividades.data = pieData
        pieChartActividades.description.isEnabled = false
        pieChartActividades.setEntryLabelColor(Color.BLACK)
        pieChartActividades.centerText = "Actividades"
        pieChartActividades.animateY(1000)
        pieChartActividades.invalidate()
    }


}
