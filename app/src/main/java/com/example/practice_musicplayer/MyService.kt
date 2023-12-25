package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.utils.ApplicationClass
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

@Suppress("DEPRECATION")
class MyService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null
    private val channelid = "3"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChanel()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playAudio()
        showNotification()

        return START_STICKY
    }

    private fun playAudio() {
        mediaPlayer!!.apply {
            reset()
            setDataSource("https://aydym.com/audioFiles/original/2023/10/24/17/42/944dc23f-c4cf-4267-8122-34b3eb2bada8.mp3")
            prepareAsync()
            setOnPreparedListener { start() }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag", "InlinedApi")
    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 3, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val exitIntent =
            Intent(baseContext, MyBroadcastReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext, 3, exitIntent, flag
        )


        val notification = Notification
            .Builder(this, channelid)
            .setContentText("Music Player")
            .setSmallIcon(R.drawable.play)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.close_notification, "Previous", exitPendingIntent)
            .build()

        ServiceCompat.startForeground(
            this, 3, notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )

    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelid, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}