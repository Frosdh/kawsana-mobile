package com.example.app_practicas_m5a.presentacion.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
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

class MostrarGraficoLider : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var tvProgresoProyecto: TextView
    private lateinit var barChartProgreso: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_grafico_lider)

        barChart = findViewById(R.id.barChart)
        tvProgresoProyecto = findViewById(R.id.tvProgresoProyecto)
        barChartProgreso = findViewById(R.id.barChart)

        val nombres = intent.getStringArrayListExtra("nombres")
        val valores = intent.getFloatArrayExtra("valores")

        if (nombres != null && valores != null) {
            mostrarGrafico(nombres, valores)
        } else {
            cargarProyectoConProgreso()
        }
    }

    private fun mostrarGrafico(nombres: ArrayList<String>, valores: FloatArray) {
        val entries = ArrayList<BarEntry>()
        for (i in valores.indices) {
            entries.add(BarEntry(i.toFloat(), valores[i]))
        }

        val dataSet = BarDataSet(entries, "Progreso de proyectos (%)")
        dataSet.color = resources.getColor(R.color.teal_700, null)

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.description.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(nombres)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.axisMaximum = 100f
        barChart.axisRight.isEnabled = false

        barChart.invalidate()
    }

    private fun cargarProyectoConProgreso() {
        lifecycleScope.launch {
            val proyectos = withContext(Dispatchers.IO) {
                ProyectoDisponobleDao.obtenerTodosLosProyectosConProgreso()
            }

            if (proyectos.isNotEmpty()) {
                val primerProyecto = proyectos[0]
                tvProgresoProyecto.text = "Progreso: ${primerProyecto.progreso}%"
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
            val progresoRaw = proyecto.progreso?.lowercase()?.trim() ?: ""
            val progresoNumerico = when {
                progresoRaw.contains("terminado") -> 100
                progresoRaw.contains("en progreso") || progresoRaw.contains("en_progreso") -> 50
                else -> proyecto.progreso?.filter { it.isDigit() }?.toIntOrNull() ?: 0
            }
            entries.add(BarEntry(index.toFloat(), progresoNumerico.toFloat()))
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
