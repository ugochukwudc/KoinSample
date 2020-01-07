package com.example.koininjectionsampleproject

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    // Insert with koin
    private val mConnectivityManager: AppConnectivityManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(this)

//        val mConnectivityManager: AppConnectivityManager = AppConnectivityManager(this)
        mConnectivityManager.connectionState.observe(this, Observer {
            textView.text = it.message
            imageView.setImageResource(it.drawableRes)
        })

        val notificationChannelGroup: NotificationChannelGroup = NotificationChannelGroup("MY_CHANNEL_GROUP_ID", "Koin Sample Application Notification Group")
        val notificationChannel: NotificationChannel = NotificationChannel("MY_CHANNEL_ID", "Default Notifications", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.group = "MY_CHANNEL_GROUP_ID"
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationChannel.enableVibration(true)
        notificationManagerCompat.createNotificationChannelGroup(notificationChannelGroup)
        notificationManagerCompat.createNotificationChannel(notificationChannel)
        var notificationCount = 1000009
        val builder = Notification.Builder(this, "MY_CHANNEL_ID")
        button.setOnClickListener {
            Log.d("TAG", "Button clicked Event $notificationCount, are notifications enabled = ${notificationManagerCompat.areNotificationsEnabled()}")
            notificationManagerCompat.notify(notificationCount,
                builder
                    .setGroup("MY_CHANNEL_GROUP_ID")
                    .setContentTitle("My Notification $notificationCount")
                    .setContentText("Context Text for notification $notificationCount")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()
            )
            notificationCount++
        }

    }
}
