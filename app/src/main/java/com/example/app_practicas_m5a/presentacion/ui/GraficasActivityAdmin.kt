package com.example.app_practicas_m5a.presentacion.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.BarrioDaoProgresoGrafico
import com.example.app_practicas_m5a.data.model.BarrioProgreso
import com.example.app_practicas_m5a.data.model.BarrioUsuariosLideres
import com.example.app_practicas_m5a.data.model.ResumenActividades
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Random

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
            val top6Barrios = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerTop6Barrios()
            }
            if (top6Barrios.isNotEmpty()) {
                mostrarGraficaBarra(top6Barrios)
            }

            val usuariosYLideres = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerUsuariosYLideresPorBarrio()
            }
            if (usuariosYLideres.isNotEmpty()) {
                mostrarGraficaPastelBarrios(usuariosYLideres)
            }

            val resumenActividades = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerResumenActividades()
            }
            mostrarGraficaPastelActividades(resumenActividades)
        }
    }

    private fun mostrarGraficaBarra(datos: List<BarrioProgreso>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        datos.forEachIndexed { index, dato ->
            entries.add(BarEntry(index.toFloat(), dato.progreso))
            labels.add(dato.nombreBarrio)
        }

        val dataSet = BarDataSet(entries, "Top 6 Barrios por Progreso (%)")

        val colores = mutableListOf<Int>()
        val random = Random()
        entries.indices.forEach {
            colores.add(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
        }
        dataSet.colors = colores
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        barChart.data = BarData(dataSet)
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -30f

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description = Description().apply { text = "" }
        barChart.setDrawValueAboveBar(true)
        barChart.setFitBars(true)
        barChart.legend.isEnabled = true

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

            val random = java.util.Random()
            repeat(20) {
                val r = random.nextInt(206) + 50
                val g = random.nextInt(206) + 50
                val b = random.nextInt(206) + 50
                add(Color.rgb(r, g, b))
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
        pieChartBarrios.isDrawHoleEnabled = true
        pieChartBarrios.holeRadius = 40f
        pieChartBarrios.setEntryLabelTextSize(12f)
        pieChartBarrios.animateY(1000)
        pieChartBarrios.invalidate()
    }

    private fun mostrarGraficaPastelActividades(resumen: ResumenActividades) {
        val total = resumen.aprobadas + resumen.noAprobadas
        if (total == 0) {
            pieChartActividades.clear()
            pieChartActividades.setNoDataText("No hay actividades registradas")
            pieChartActividades.invalidate()
            return
        }

        val entries = listOf(
            PieEntry(resumen.aprobadas.toFloat(), "Aprobadas"),
            PieEntry(resumen.noAprobadas.toFloat(), "No aprobadas")
        )

        val dataSet = PieDataSet(entries, "Estado de Actividades")

        val random = Random()
        val colores = listOf(
            Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)),
            Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
        )
        dataSet.colors = colores

        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)
        pieChartActividades.setUsePercentValues(true)
        pieChartActividades.data = pieData
        pieChartActividades.description.isEnabled = false
        pieChartActividades.setEntryLabelColor(Color.BLACK)
        pieChartActividades.centerText = "Actividades"
        pieChartActividades.isDrawHoleEnabled = true
        pieChartActividades.holeRadius = 40f
        pieChartActividades.setEntryLabelTextSize(12f)
        pieChartActividades.animateY(1000)
        pieChartActividades.invalidate()
    }
}
