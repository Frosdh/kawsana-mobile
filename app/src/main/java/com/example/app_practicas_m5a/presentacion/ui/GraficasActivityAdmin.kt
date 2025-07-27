package com.example.app_practicas_m5a.presentacion.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_practicas_m5a.R
import com.example.app_practicas_m5a.data.dao.BarrioDaoProgresoGrafico
import com.example.app_practicas_m5a.data.model.BarrioProgreso
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GraficasActivityAdmin : AppCompatActivity() {

    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficas_admin)

        barChart = findViewById(R.id.barChart)

        lifecycleScope.launch {
            val datos = withContext(Dispatchers.IO) {
                BarrioDaoProgresoGrafico.obtenerTop6Barrios()
            }

            if (datos.isNotEmpty()) {
                mostrarGrafica(datos)
            }
        }
    }

    private fun mostrarGrafica(datos: List<BarrioProgreso>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for ((index, dato) in datos.withIndex()) {
            entries.add(BarEntry(index.toFloat(), dato.progreso))
            labels.add(dato.nombreBarrio)
        }

        val dataSet = BarDataSet(entries, "Top 6 Barrios por Progreso (%)")

        // ✅ Colores aleatorios para cada barra
        val colores = mutableListOf<Int>()
        val random = java.util.Random()
        for (i in entries.indices) {
            val color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
            colores.add(color)
        }
        dataSet.colors = colores

        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -30f

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description = com.github.mikephil.charting.components.Description().apply { text = "" }

        // ✅ Animación y refresco
        barChart.animateY(1500)
        barChart.invalidate()
    }

}
