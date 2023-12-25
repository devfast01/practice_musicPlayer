package com.example.practice_musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.fragments.NowPlaying
import com.example.practice_musicplayer.utils.ApplicationClass

@Suppress("DEPRECATION")
class MyBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.EXIT -> {
                exitApplicationNotification()
            }
        }
    }


    fun prevNextMusic(increment: Boolean, context: Context) {
        try {
            MusicInterface.musicService!!.initSong()
            Glide.with(context)
                .load(getImageArt(MusicInterface.musicList[MusicInterface.songPosition].coverArtUrl))
                .apply(
                    RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
                ).into(MusicInterface.binding.interfaceCover)

            MusicInterface.binding.interfaceSongName.text =
                MusicInterface.musicList[MusicInterface.songPosition].name
            MusicInterface.binding.interfaceArtistName.text =
                MusicInterface.musicList[MusicInterface.songPosition].artist
            Glide.with(context)
                .load(getImageArt(MusicInterface.musicList[MusicInterface.songPosition].coverArtUrl))
                .apply(
                    RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
                ).into(NowPlaying.binding.fragmentImage)
            NowPlaying.binding.fragmentTitle.text =
                MusicInterface.musicList[MusicInterface.songPosition].name
            NowPlaying.binding.fragmentAlbumName.text =
                MusicInterface.musicList[MusicInterface.songPosition].artist
            playMusic()

        } catch (e: Exception) {
            Log.e("AdapterView", e.toString())
        }
    }

    private fun playMusic() {
        MusicInterface.musicService!!.audioManager.requestAudioFocus(
            MusicInterface.musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        )
        MusicInterface.isPlaying = true
        MusicInterface.binding.interfacePlay.setImageResource(R.drawable.pause)
        MusicInterface.musicService!!.mediaPlayer!!.start()
        MusicInterface.musicService!!.showNotification(R.drawable.pause_notification)
        NowPlaying.binding.fragmentButton.setImageResource(R.drawable.pause_now)

    }

    private fun pauseMusic() {
        MusicInterface.musicService!!.audioManager.abandonAudioFocus(MusicInterface.musicService)
        MusicInterface.isPlaying = false
        MusicInterface.binding.interfacePlay.setImageResource(R.drawable.play)
        MusicInterface.musicService!!.mediaPlayer!!.pause()
        MusicInterface.musicService!!.showNotification(R.drawable.play_notification)
        NowPlaying.binding.fragmentButton.setImageResource(R.drawable.play_now)

    }

}