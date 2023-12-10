package com.example.cargoshipappkotlin

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class LineGraph(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val graph: GraphView
    private val textView: TextView

    private val dataPoints: MutableList<Pair<Double, Float>> = mutableListOf()
    private val series: LineGraphSeries<DataPoint> = LineGraphSeries()

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        // Create a GraphView
        graph = GraphView(context)
        graph.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        // Create a TextView for displaying data points
        textView = TextView(context)
        textView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        textView.gravity = Gravity.CENTER

        // Add the GraphView and TextView to the layout
        addView(graph)
        addView(textView)

        // Customize GraphView settings if needed
        graph.title = ""
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        // Add a series to the graph
        graph.addSeries(series)
    }

    fun updateRotationLineGraph(rotation: Float, graphTitle :String) {
        graph.title = graphTitle
        graph.gridLabelRenderer.horizontalAxisTitle = "Data Points"
        graph.gridLabelRenderer.verticalAxisTitle = "m/s^2"
        // Add data point to the list
        dataPoints.add(Pair(dataPoints.size.toDouble(), rotation))

        // Clear the series and ad`d all data points
        series.resetData(dataPoints.map { DataPoint(it.first, it.second.toDouble()) }.toTypedArray())

        // Update the graph viewport to show the latest data points
        graph.viewport.setMinX(dataPoints.firstOrNull()?.first ?: 0.0)
        graph.viewport.setMaxX(dataPoints.lastOrNull()?.first ?: 0.0)

        // Scale the y-axis based on accelerometer ranges (adjust as needed)
        graph.viewport.setMinY(-10.0)
        graph.viewport.setMaxY(10.0)

    }
}
