package com.vanka.suraksha.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vanka.suraksha.MainActivity
import com.vanka.suraksha.R
import kotlin.random.Random

class NotificationClass: FirebaseMessagingService() {

    private val channelId = "Memllo"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
        createNotificationChannel(manager as NotificationManager)

        var inte = PendingIntent.getActivities(this,0, arrayOf(intent), PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this,channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.email_foreground)
            .setAutoCancel(true)
            .setContentIntent(inte)
            .build()
        // manager.notify(Random.nextInt(),notification)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(applicationContext)
            .notify(Random.nextInt(), notification)



    }
    private fun createNotificationChannel(manager: NotificationManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =   NotificationChannel(channelId,"memllochat", NotificationManager.IMPORTANCE_HIGH)
            channel.description ="New Chat"
            channel.enableLights(true)
            manager.createNotificationChannel(channel)
        }
    }


}