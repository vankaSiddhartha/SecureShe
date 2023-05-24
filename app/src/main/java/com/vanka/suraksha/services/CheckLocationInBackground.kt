package com.vanka.suraksha.services
import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*


class CheckLocationInBackground: Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(2, createNotification()) // Start service as a foreground service
        requestLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "your_channel_id"
        val channelName = "Your Channel Name"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        // Build a notification using NotificationCompat
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Tracking location...")
            .setSmallIcon(com.vanka.suraksha.R.drawable.baseline_location_on_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return notificationBuilder.build()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000 // Update interval in milliseconds
            fastestInterval = 500 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(p0: LocationResult) {
                p0
                for (location in p0.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    updateNotificationText(latitude, longitude)

                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } else {
            Toast.makeText(
                this,
                "Location permission not granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNotificationText(latitude: Double, longitude: Double) {
        val channelId = "your_channel_id"
        val channelName = "Your Channel Name"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = if (checkDangerArea(latitude, longitude)) {
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Alert")
                .setContentText("You are in a red zone")
                .setSmallIcon(com.vanka.suraksha.R.drawable.baseline_location_on_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        } else {
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Hi")
                .setContentText("You are in a green zone")
                .setSmallIcon(com.vanka.suraksha.R.drawable.baseline_location_on_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

        startForeground(2, notificationBuilder.build())
    }







    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // Radius of the Earth in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }
    fun checkDangerArea(latitude: Double,longitude: Double):Boolean{
        val locationA = arrayOf(
            Pair(18.288002, 83.891958), // Srikakulam Collectorate
            Pair(18.304981, 83.895118), // Srikakulam Railway Station
            Pair(18.298537, 83.898674), // Srikakulam Bus Stand
            Pair(18.304524, 83.891512), // Srikakulam Beach
            Pair(18.291556, 83.894678), // Ambedkar Circle
            Pair(18.288685, 83.897509), // Jetti Subbarao College
            Pair(18.289825, 83.891877), // LIC Building
            Pair(18.291250, 83.892648), // District Court, Srikakulam
            Pair(18.295917, 83.948768), // Srikurmam Temple
            Pair(18.315674, 83.876079),// LN Peta



        )

        var userLatitude = latitude // Latitude of user's location
        var userLongitude = longitude // Longitude of user's location
        var isWithinRadius = false

        for (location in locationA) {
            val distance = calculateDistance(
                location.first,
                location.second,
                userLatitude,
                userLongitude
            )

            if (distance <= 1.0) {
                isWithinRadius = true
                break
            }
        }

        return isWithinRadius
    }
}