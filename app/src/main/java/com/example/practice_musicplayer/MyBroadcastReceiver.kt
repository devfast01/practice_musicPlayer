package com.example.practice_musicplayer

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.ContentValues
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
            ApplicationClass.PREVIOUS -> {
                prevNextMusic(increment = false, context = context!!)
            }

            ApplicationClass.PLAY -> {
                if (MusicInterface.isPlaying) pauseMusic() else playMusic()
            }

            ApplicationClass.NEXT -> {
                prevNextMusic(increment = true, context = context!!)
                MusicInterface.counter--
            }

            ApplicationClass.EXIT -> {
                exitApplicationNotification()
            }
            /*
                        AudioManager.ACTION_HEADSET_PLUG -> {
                            val state = intent.getIntExtra("state", -1)
                            if (state == 0) {
                                pauseMusic()
                            } else{
                                Log.d(TAG, "onReceive: HeadSet Plugged")
                            }
                        }

             */

            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Log.d(ContentValues.TAG, "onReceive: ${state.toString()}")
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        pauseMusic()
                    }

                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        pauseMusic()
                    }
                }
            }
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


    private fun pauseMusic() {
        MusicInterface.musicService!!.audioManager.abandonAudioFocus(MusicInterface.musicService)
        MusicInterface.isPlaying = false
        MusicInterface.binding.interfacePlay.setImageResource(R.drawable.play)
        MusicInterface.musicService!!.mediaPlayer!!.pause()
        MusicInterface.musicService!!.showNotification(R.drawable.play_notification)
        NowPlaying.binding.fragmentButton.setImageResource(R.drawable.play_now)

    }

}