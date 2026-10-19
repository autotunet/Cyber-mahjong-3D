package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HeadOrientation(
    val pitch: Float = 0f,  // Look up/down in degrees
    val yaw: Float = 0f,    // Turn left/right in degrees
    val roll: Float = 0f
)

class OrientationSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val _orientationFlow = MutableStateFlow(HeadOrientation())
    val orientationFlow: StateFlow<HeadOrientation> = _orientationFlow.asStateFlow()

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var baseYawOffset = 0f
    private var isCalibrated = false

    fun startListening() {
        rotationSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    fun calibrateCenter() {
        isCalibrated = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val rawPitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
        val rawYaw = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        val rawRoll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

        if (!isCalibrated) {
            baseYawOffset = rawYaw
            isCalibrated = true
        }

        val relativeYaw = (rawYaw - baseYawOffset)
        _orientationFlow.value = HeadOrientation(
            pitch = rawPitch.coerceIn(-60f, 60f),
            yaw = relativeYaw,
            roll = rawRoll
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
