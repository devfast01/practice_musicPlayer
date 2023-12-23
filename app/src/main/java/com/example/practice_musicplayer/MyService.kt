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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

@Suppress("DEPRECATION")
class MyService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private var audioUrl: String? = null
    private val channelid = "12"


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChanel()
    }

    fun playAudio() {
        mediaPlayer.apply {
            reset()
            setDataSource("https://aydym.com/audioFiles/original/2023/10/24/17/42/944dc23f-c4cf-4267-8122-34b3eb2bada8.mp3")
            prepareAsync()
            setOnPreparedListener { start() }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playAudio()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotification()
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag", "InlinedApi")
    private fun showNotification() {
        val notificationIntent = Intent(this, MusicInterface::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 3, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = Notification
            .Builder(this, channelid)
            .setContentText("Music Player")
            .setSmallIcon(R.drawable.play)
            .setContentIntent(pendingIntent)
            .build()

        ServiceCompat.startForeground(this, 5, notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )

    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val serviceChannel = NotificationChannel(
                channelid, "My Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}