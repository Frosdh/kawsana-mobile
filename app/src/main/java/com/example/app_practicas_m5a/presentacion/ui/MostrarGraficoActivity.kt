package com.example.app_practicas_m5a.presentacion.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.app_practicas_m5a.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MostrarGraficoActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_grafico)

        barChart = findViewById(R.id.barChart)

        val nombres = intent.getStringArrayListExtra("nombres")
        val valores = intent.getFloatArrayExtra("valores")

        if (nombres != null && valores != null) {
            mostrarGrafico(nombres, valores)
        }
    }

    private fun mostrarGrafico(nombres: ArrayList<String>, valores: FloatArray) {
        val entradas = valores.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entradas, "Progreso por Barrio").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#81C784"),
                Color.parseColor("#388E3C"),
                Color.parseColor("#A5D6A7")
            )
            valueTextColor = Color.DKGRAY
            valueTextSize = 14f
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        barChart.apply {
            data = barData
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(nombres)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
            }
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 10f
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = true
            animateY(1000)
            invalidate()
        }
    }
}
