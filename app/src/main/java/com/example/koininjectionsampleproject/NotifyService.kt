package com.example.koininjectionsampleproject

import android.app.*
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.koininjectionsampleproject.MainActivity.Companion.KEY_NOTIFICATION_ID
import org.koin.android.ext.android.inject

class NotifyService : Service() {
    private val notificationManagerCompat: NotificationManagerCompat by inject()
    private var notificationId: Int = 10001

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationId = intent?.getIntExtra(KEY_NOTIFICATION_ID, 10001) ?: 10001
        val notificationChannelGroup = NotificationChannelGroup("MY_CHANNEL_GROUP_ID", "Koin Sample Application Notification Group")
        val notificationChannel = NotificationChannel("MY_CHANNEL_ID", "Default Notifications", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.group = "MY_CHANNEL_GROUP_ID"
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationChannel.enableVibration(true)
        notificationManagerCompat.createNotificationChannelGroup(notificationChannelGroup)
        notificationManagerCompat.createNotificationChannel(notificationChannel)
        val builder = Notification.Builder(this, "MY_CHANNEL_ID")
        notificationManagerCompat.notify(
            notificationId,
            builder
                .setGroup("MY_CHANNEL_GROUP_ID")
                .setContentTitle("My Notification $notificationId")
                .setContentText("Context Text for notification $notificationId")
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setActions(
                    Notification.Action
                        .Builder(
                            Icon.createWithResource(this, android.R.drawable.ic_menu_help),
                            "Start",
                            getPendingIntent()
                        )
                        .build()
                )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        )

        return START_STICKY_COMPATIBILITY
    }

    private fun getPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            notificationId,
            Intent(applicationContext, MainActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(MainActivity.KEY_NOTIFICATION_ID, notificationId)
            ,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
}
