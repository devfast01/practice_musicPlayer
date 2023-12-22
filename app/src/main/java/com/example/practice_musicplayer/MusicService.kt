package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.utils.ApplicationClass

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    val broadcastReceiver: MyBroadcastReceiver = MyBroadcastReceiver()

    companion object {
        lateinit var playPendingIntent: PendingIntent
    }

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }
    override fun onAudioFocusChange(focusChange: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseButton: Int) {
        val intent = Intent(baseContext, MusicInterface::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("index", MusicInterface.songPosition)
        intent.putExtra("class", "Now Playing Notification")

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val prevIntent =
            Intent(baseContext, MyBroadcastReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext, 3, prevIntent, flag

        )
    }

}