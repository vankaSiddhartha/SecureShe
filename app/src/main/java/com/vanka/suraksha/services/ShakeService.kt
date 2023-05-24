package com.vanka.suraksha.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vanka.suraksha.MainActivity
import com.vanka.suraksha.R

class ShakeService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var shakeTimestamp: Long = 0
    private var shakeCount: Int = 0

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = calculateAcceleration(x, y, z)
        val currentTimestamp = System.currentTimeMillis()

        if (isShakeDetected(acceleration)) {
            if (shakeTimestamp + SHAKE_INTERVAL_MS > currentTimestamp) {
                // Shake event occurred within the interval, increment shake count
                shakeCount++
                if (shakeCount >= MIN_SHAKES) {
                    shakeCount = 0
                    showNotification()

                }
            } else {
                // Shake event occurred after the interval, reset shake count
                shakeCount = 1
            }
            shakeTimestamp = currentTimestamp
        }
    }

    private fun calculateAcceleration(x: Float, y: Float, z: Float): Float {
        return Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }

    private fun isShakeDetected(acceleration: Float): Boolean {
        return acceleration > SHAKE_THRESHOLD
    }

    private fun showNotification() {
        val channelId = "shake_channel"
        val channelName = "Shake Channel"
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pending1Intent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create a notification builder
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_location_on_24)
            .setContentTitle("Shake Detected")
            .setContentText("You shook your phone!")
            .setContentIntent(pending1Intent)
            .setAutoCancel(true)

        // Create an intent to open the app when the notification is tapped
        val appIntent = packageManager.getLaunchIntentForPackage("com.vanka.suraksha")
        val pendingIntent = PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pendingIntent)

        // Display the notification
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        // Schedule notification dismissal after 10 seconds
//        val dismissDelay = 1000L // 10 seconds
//        Handler().postDelayed({
//            notificationManager.cancel(NOTIFICATION_ID)
//        }, dismissDelay)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val SHAKE_THRESHOLD = 8.0f // Adjust this value as needed
        private const val SHAKE_INTERVAL_MS = 10000 // Adjust this value as needed
        private const val MIN_SHAKES = 1000 // Adjust this value as needed
        private const val NOTIFICATION_ID = 123
    }
}
