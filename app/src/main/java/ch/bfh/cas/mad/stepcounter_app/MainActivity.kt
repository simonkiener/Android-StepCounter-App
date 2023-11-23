package ch.bfh.cas.mad.stepcounter_app

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var textViewData: TextView
    private lateinit var sensorManager : SensorManager
    private var stepCounter: Sensor? = null
    private lateinit var actiivtyRegcognitionPermissionRequest: ActivityResultLauncher<String>
    private lateinit var eventListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewData = findViewById(R.id.textview_data)

        actiivtyRegcognitionPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                when {
                    isGranted -> startStepCounter()
                    else -> {
                        requestActivityRecognitionPermission()
                    }
                }
            }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        eventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                textViewData.text = event.values[0].toString()
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            }
        }
    }

    private fun startStepCounter() {
        stepCounter?.let {
            if(checkSelfPermission("android.permission.ACTIVITY_RECOGNITION") != PackageManager.PERMISSION_GRANTED) {
                requestActivityRecognitionPermission()
            } else {
                sensorManager.registerListener(
                    eventListener,
                    stepCounter,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    private fun requestActivityRecognitionPermission() {
        actiivtyRegcognitionPermissionRequest.launch("android.permission.ACTIVITY_RECOGNITION")
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            eventListener,
            stepCounter,
            SensorManager.SENSOR_DELAY_NORMAL
        )

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(eventListener)
    }
}
