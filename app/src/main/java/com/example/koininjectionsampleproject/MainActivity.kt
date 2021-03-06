package com.example.koininjectionsampleproject

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {
    // Insert with koin
    private val connectivityManager: AppConnectivityManager by inject()
    private val notificationManagerCompat: NotificationManagerCompat by inject {
        parametersOf(this)
    }

    private val alarmManager: AlarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var notificationId = 1000009

        if (intent.isLaunchedFromNotification()) {
            // Show a Toast and Dismiss the notification
            notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0)
            Toast.makeText(
                this,
                "Application Launched from Notification $notificationId",
                Toast.LENGTH_SHORT
            )
                .show()
            // dismiss notification
            notificationManagerCompat.cancel(notificationId)
            notificationId++
        }

        connectivityManager.connectionState.observe(this, Observer {
            textView.text = it.message
            imageView.setImageResource(it.drawableRes)
        })

//        val builder: Notification.Builder = setUpNotification()
        button.setOnClickListener {
            Log.d("TAG", "Button clicked Event $notificationId")
//            showNotification(notificationId, builder)
//            setUpAlarmWithService(notificationId)
            setUpAlarmWithReceiver(notificationId)
            notificationId++
        }

    }

    private fun showNotification(notificationId: Int, builder: Notification.Builder) {
        Log.d(
            "TAG",
            "ShowNotification Event $notificationId, are notifications enabled = ${notificationManagerCompat.areNotificationsEnabled()}"
        )
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
                            PendingIntent.getActivity(
                                this,
                                notificationId,
                                Intent(applicationContext, MainActivity::class.java)
                                    .setAction(Intent.ACTION_VIEW)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .putExtra(KEY_NOTIFICATION_ID, notificationId)
                                ,
                                PendingIntent.FLAG_CANCEL_CURRENT
                            )
                        )
                        .build()
                )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        )

    }

    private fun setUpNotification(): Notification.Builder {
        val notificationChannelGroup = NotificationChannelGroup(
            "MY_CHANNEL_GROUP_ID",
            "Koin Sample Application Notification Group"
        )
        val notificationChannel = NotificationChannel(
            "MY_CHANNEL_ID",
            "Default Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.group = "MY_CHANNEL_GROUP_ID"
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationChannel.enableVibration(true)
        notificationManagerCompat.createNotificationChannelGroup(notificationChannelGroup)
        notificationManagerCompat.createNotificationChannel(notificationChannel)
        return Notification.Builder(this, "MY_CHANNEL_ID")
    }

    private fun setUpAlarmWithService(notificationId: Int){
        Log.d(
            "TAG",
            "setUpAlarmWithService $notificationId, are notifications enabled = ${notificationManagerCompat.areNotificationsEnabled()}"
        )

        AlarmManagerCompat.setExact(
            alarmManager,
            AlarmManager.RTC,
             System.currentTimeMillis() + 60_000L,
            PendingIntent.getService(
                this,
                10001,
                Intent(this, NotifyService::class.java)
                    .putExtra(KEY_NOTIFICATION_ID, notificationId),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        )
    }

    private fun setUpAlarmWithReceiver(notificationId: Int){
        Log.d(
            "TAG",
            "setUpAlarmWithReceiver $notificationId, are notifications enabled = ${notificationManagerCompat.areNotificationsEnabled()}"
        )
        val calendar = Calendar.getInstance()

        AlarmManagerCompat.setExact(
            alarmManager,
            AlarmManager.RTC,
            System.currentTimeMillis() + 10_000L,
            PendingIntent.getBroadcast(
                this,
                10001,
                Intent(this, NotifyAlarmReceiver::class.java)
                    .putExtra(KEY_NOTIFICATION_ID, notificationId),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        )
    }


    private fun Intent?.isLaunchedFromNotification(): Boolean {
        if (this == null) return false
        return action == Intent.ACTION_VIEW && hasExtra(KEY_NOTIFICATION_ID)
    }

    companion object {
        const val KEY_NOTIFICATION_ID = "NOTIFICATION_ID"
    }
}
