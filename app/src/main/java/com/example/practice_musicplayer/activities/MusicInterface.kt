package com.example.practice_musicplayer.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MainActivity
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.MusicService
import com.example.practice_musicplayer.MyService
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.databinding.ActivityMusicInterfaceBinding
import com.example.practice_musicplayer.formatDuration
import com.example.practice_musicplayer.fragments.NowPlaying
import com.example.practice_musicplayer.getImageArt


@Suppress("DEPRECATION")
class MusicInterface : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMusicInterfaceBinding
        lateinit var songList: ArrayList<MusicClass>
        lateinit var musicList: ArrayList<MusicClass>
        var musicService: MusicService? = null
        var myService: MyService? = null
        var fIndex: Int = -1
        var isLiked: Boolean = false
        var counter: Int = 0
            set(value) {
                field = kotlin.math.max(value, 0)
            }
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var isRepeating: Boolean = false
        var isShuffling: Boolean = false
        var musicUrl =
            "https://aydym.com/audioFiles/original/2023/10/24/17/42/944dc23f-c4cf-4267-8122-34b3eb2bada8.mp3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.interfaceSongName.isSelected = true

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.interfacePlay.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }
    }
    private fun initSong() {
        try {
            if (myService!!.mediaPlayer == null) myService!!.mediaPlayer = MediaPlayer()
            myService!!.mediaPlayer!!.reset()
            myService!!.mediaPlayer!!.setDataSource("https://aydym.com/audioFiles/original/2023/10/24/17/42/944dc23f-c4cf-4267-8122-34b3eb2bada8.mp3")
            myService!!.mediaPlayer!!.prepare()
            binding.interfacePlay.setImageResource((R.drawable.pause))
            myService!!.mediaPlayer!!.setOnCompletionListener(this)
            playMusic()

        } catch (e: Exception) {
            return
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (myService == null) {
            val binder = service as MyService.MyBinder
            myService = binder.currentService()
            myService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            myService!!.audioManager.requestAudioFocus(
                myService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
            )
            Log.e("isPlay", myService.toString())
        }
        Log.e("isPlay", myService.toString())
        initSong()
        musicService!!.seekBarHandler()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        myService = null
        Log.e("disConnectred", myService.toString())
    }


//    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        if (musicService == null) {
//            val binder = service as MusicService.MyBinder
//            musicService = binder.currentService()
//            musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//            musicService!!.audioManager.requestAudioFocus(
//                musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
//            )
//            Log.e("isPlay", musicService.toString())
//        }
//        Log.e("isPlay", musicService.toString())
//        initSong()
//        musicService!!.seekBarHandler()
//    }
//
//    override fun onServiceDisconnected(name: ComponentName?) {
//        musicService = null
//    }


    private fun setLayout() {
        try {
            Glide.with(this).load(getImageArt(musicList[songPosition].coverArtUrl)).apply(
                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
            ).into(binding.interfaceCover)

            binding.interfaceSongName.text = musicList[songPosition].name
            binding.interfaceArtistName.text = musicList[songPosition].artist

        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        try {
            Toast.makeText(this, "++++++++++++", Toast.LENGTH_SHORT).show()
            myService?.let {
                it.audioManager.requestAudioFocus(
                    it, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
                )
                isPlaying = true
                it.mediaPlayer!!.start()  // Check if mediaPlayer is not null
                binding.interfacePlay.setImageResource((R.drawable.pause))
                it.showNotification()
                NowPlaying.binding.fragmentButton.setImageResource(R.drawable.pause_now)

                myService!!.audioManager.requestAudioFocus(
                    myService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
                )
            }
        }catch (e: Exception) {
            return
        }
    }

    private fun pauseMusic() {
        try {
            myService!!.audioManager.abandonAudioFocus(myService)
            isPlaying = false
            myService!!.mediaPlayer!!.pause()
            binding.interfacePlay.setImageResource((R.drawable.play))
            myService!!.showNotification()
            NowPlaying.binding.fragmentButton.setImageResource(R.drawable.play_now)
        } catch (e: Exception) {
            return
        }
    }


    override fun onCompletion(mp: MediaPlayer?) {
        setLayout()
        initSong()
        counter--
    }


    override fun onResume() {
        super.onResume()
        overridePendingTransition(com.google.android.material.R.anim.mtrl_bottom_sheet_slide_in, 0)
    }
}