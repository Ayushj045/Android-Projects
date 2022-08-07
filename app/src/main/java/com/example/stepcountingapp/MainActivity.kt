package com.example.stepcountingapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(),SensorEventListener {
    //Added sensorEventListener the mainActivity class
    // Implement all the member in class MainActivity
    // after adding SensorEventListner

    // we have assigned sensorManger to nullable
    private var sensorManager: SensorManager? = null

    // creating a variable which will give running status
    // and it has been giving boolean value as false
    private var running = false

    // creating a variable which will count total steps
    // and it has been given the value of 0 float
    private var totalSteps = 0f

    // creating a variable whic count previous total
    // steps and it has also been given the value of 0 float
    private var previousTotalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
        resetSteps()

        // Adding a context of SENSOR_SERVICE as Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }


    override fun onResume() {
        super.onResume()
        running = true
        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            // This will give a Toast message to user if there is no sensor in device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            //Rate suitable for usre interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        var stepsTaken = findViewById<TextView>(R.id.stepsTaken)

        if (running) {
            totalSteps = event!!.values[0]

            //Current steps calculated by taking the difference of total steps
            //and pervious steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            // it will show the current steps to user
            stepsTaken.text = ("$currentSteps")
        }
    }

    fun resetSteps() {
        var stepsTaken = findViewById<TextView>(R.id.stepsTaken)
        stepsTaken.setOnClickListener {
            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            stepsTaken.text = 0.toString()

            // this will save data
            saveData()

            true
        }
    }

    private fun saveData() {
        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()

    }
    private fun loadData(){
        // in this fun we will retrieve data
        val sharedPreference = getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val savedNumber = sharedPreference.getFloat("key1",0f)

        //Log.d is used for debugging purposes
        Log.d("MainActivity","$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
       // we do not have to write anything in this fun for this app
    }

}
