package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.ServiceCompat
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.fragments.NowPlaying
import com.example.practice_musicplayer.utils.ApplicationClass

@Suppress("DEPRECATION")
class MyService : Service(), AudioManager.OnAudioFocusChangeListener {
    var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null
    private var myBinder = MyBinder()
    private val channelid = "3"
    lateinit var audioManager: AudioManager
    private lateinit var mediaSession: MediaSessionCompat

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChanel()
    }

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "Music")
        return myBinder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playAudio()
        showNotification()

        return START_STICKY
    }

    private fun playAudio() {
        mediaPlayer?.apply {
            reset()
            setDataSource(MusicInterface.musicUrl)
            prepareAsync()
            setOnPreparedListener { start() }
        }
        initSong()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag", "InlinedApi")
    fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 3, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            .build()

        ServiceCompat.startForeground(
            this, 3, notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    inner class MyBinder : Binder() {
        fun currentService(): MyService {
            return this@MyService
        }
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

    fun initSong() {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            MusicInterface.myService?.let {
                it.mediaPlayer!!.reset()
                it.mediaPlayer!!.setDataSource(MusicInterface.musicList!![MusicInterface.songPosition].url)
                it.mediaPlayer!!.prepare()
                it.showNotification()
                MusicInterface.binding.interfacePlay.setImageResource((R.drawable.pause))
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
    }


    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            //pause music
            MusicInterface.binding.interfacePlay.setImageResource(R.drawable.play)
            MusicInterface.isPlaying = false
            NowPlaying.binding.fragmentButton.setImageResource(R.drawable.play_now)
            mediaPlayer!!.pause()
            showNotification()

        } else {
            //play music
            MusicInterface.binding.interfacePlay.setImageResource(R.drawable.pause)
            MusicInterface.isPlaying = true
            mediaPlayer!!.start()
            NowPlaying.binding.fragmentButton.setImageResource(R.drawable.pause_now)
            showNotification()
        }
    }
}