package com.example.practice_musicplayer

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onAudioFocusChange(focusChange: Int) {
        TODO("Not yet implemented")
    }
}