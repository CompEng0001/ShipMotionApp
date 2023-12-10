package com.example.cargoshipappkotlin
import android.content.Context

import android.Manifest

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent

import android.os.Build
import android.os.Bundle

import android.provider.Settings
import android.util.Log

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var rotationGraphX: LineGraph
    private lateinit var rotationGraphY: LineGraph
    private lateinit var rotationGraphZ: LineGraph
    private lateinit var sensorValuesTV: TextView
    private lateinit var sensorManager: SensorManager

    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnonmeter: Sensor? = null

    private var testFileName : String = "Test.csv"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION

        createFile()

        sensorValuesTV = findViewById(R.id.sensorTV)
        rotationGraphX = findViewById(R.id.rotationGraphX)
        rotationGraphY = findViewById(R.id.rotationGraphY)
        rotationGraphZ = findViewById(R.id.rotationGraphZ)

        // Initialize SensorManager and accelerometer sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometer == null) {
            // Handle the case where the device doesn't have an accelerometer
            // You may display a message or take appropriate action
        }
        if (gyroscope == null) {
            // Handle the case where the device doesn't have an accelerometer
            // You may display a message or take appropriate action
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the accelerometer sensor listener
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the accelerometer sensor listener to save resources
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Check if the sensor type is accelerometer
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Retrieve x, y, and z positions from the accelerometer
            val xPosition = event.values[0]
            val yPosition = event.values[1]
            val zPosition = event.values[2]

            // Update the rotation graphs
            updateRotationGraphs(xPosition, yPosition, zPosition,event.sensor.name,event.accuracy,event.timestamp)
        }
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            // Retrieve x, y, and z positions from the accelerometer
            val xPosition = event.values[0]
            val yPosition = event.values[1]
            val zPosition = event.values[2]

            // Update the rotation graphs
            updateRotationGraphs(xPosition, yPosition, zPosition,event.sensor.name,event.accuracy,event.timestamp)
        }
    }

    private fun updateRotationGraphs(xRotation: Float, yRotation: Float, zRotation: Float,name: String,acc: Int,ts: Long) {
        // Update the rotation graphs with the accelerometer data
        rotationGraphX.updateRotationLineGraph(xRotation,"X")
        rotationGraphY.updateRotationLineGraph(yRotation,"Y",)
        rotationGraphZ.updateRotationLineGraph( zRotation,"Z")

        logData("$name,$ts,$acc,$xRotation,$yRotation,$zRotation\n")

        sensorValuesTV.text = "X: $xRotation Y: $yRotation Z: $zRotation"
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the sensor listener in onDestroy to avoid memory leaks
        sensorManager.unregisterListener(this)
    }

    fun createFile() {
            // Get the internal storage directory
            val internalStorageDir = filesDir

            // Create a subdirectory if needed
            val sensorDataDir = File(internalStorageDir, "Sensor_Data")
            if (!sensorDataDir.exists()) {
                ///data/data/com.example.cargoshipappkotlin/files/Sensor_Data
                sensorDataDir.mkdir()
            }

            // Your file name logic
            val baseFileName = "Test"
            val existingFiles = sensorDataDir.listFiles { _, name ->
                name.endsWith(".csv")
            }
            val counter = existingFiles?.size ?: 0
            testFileName = "$baseFileName-${counter + 1}.csv"

            // Create or open the file in the subdirectory
            val file = File(sensorDataDir, testFileName)

            try {
                // Create or overwrite the file with the specified content
                file.writeText("name,timestamp,accuracy,x,y,z\n")
                Toast.makeText(this, "File Created", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.toString()}", Toast.LENGTH_LONG).show()
            }
        }

    fun logData(data: String) {

        val internalStorageDir = filesDir

        val sensorDataDir = File(internalStorageDir, "Sensor_Data")

        try {
            val fileOutputStream = FileOutputStream(File(sensorDataDir, testFileName), true)
            fileOutputStream.write(data.toByteArray())
            fileOutputStream.close()
        } catch (e: IOException) {
            // Handle the exception if needed
            // Toast.makeText(this, " " + e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
