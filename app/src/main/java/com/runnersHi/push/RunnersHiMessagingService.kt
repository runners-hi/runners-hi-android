package com.runnersHi.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.runnersHi.MainActivity
import com.runnersHi.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** 앱이 켜져 있을 때 온 푸시를 직접 띄운다. 꺼져 있으면 시스템이 띄운다. */
@AndroidEntryPoint
class RunnersHiMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushTokenSync: PushTokenSync

    override fun onNewToken(token: String) {
        pushTokenSync.send(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val openApp = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        NotificationManagerCompat.from(this).notify(
            System.currentTimeMillis().toInt(),
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setContentIntent(openApp)
                .setAutoCancel(true)
                .build()
        )
    }

    companion object {
        private const val CHANNEL_ID = "running"

        /** 앱이 꺼져 있을 때 온 푸시도 이 채널로 오도록 시작할 때 만든다. */
        fun createChannel(context: Context) {
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "러닝 알림", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }
}
