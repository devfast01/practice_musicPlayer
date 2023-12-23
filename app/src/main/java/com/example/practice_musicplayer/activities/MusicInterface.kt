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
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MainActivity
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.MusicService
import com.example.practice_musicplayer.MyService
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.adapters.MusicAdapter
import com.example.practice_musicplayer.databinding.ActivityMusicInterfaceBinding
import com.example.practice_musicplayer.formatDuration
import com.example.practice_musicplayer.fragments.NowPlaying
import com.example.practice_musicplayer.getImageArt
import com.example.practice_musicplayer.setSongPosition
import com.example.practice_musicplayer.utils.RetrofitService
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MusicInterface : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMusicInterfaceBinding
        lateinit var songList: ArrayList<MusicClass>
        lateinit var musicList: ArrayList<MusicClass>
        var musicService: MusicService? = null
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
        var musicUrl = "https://aydym.com/audioFiles/original/2023/10/24/17/42/944dc23f-c4cf-4267-8122-34b3eb2bada8.mp3"
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
            if (isPlaying) {
                stopService(Intent(this,MyService::class.java))
                binding.interfacePlay.setImageResource(R.drawable.play)
                isPlaying = false
            }
            else {
                startService(Intent(this,MyService::class.java))
                binding.interfacePlay.setImageResource(R.drawable.pause)
                isPlaying = true
            }
            Log.e("isPlay", isPlaying.toString())
        }

    }


    private fun initActivity() {
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                Log.d("begli", MainActivity.songList.toString())
                initServiceAndPlaylist(MainActivity.songList, shuffle = false)
            }

            "Now playing" -> {
                showMusicInterfacePlaying()
            }

            "Now Playing Notification" -> {
                showMusicInterfacePlaying()
            }

            "MusicAdapterSearch" -> {
                initServiceAndPlaylist(MainActivity.musicListSearch, shuffle = false)
            }

        }
    }

    private fun showMusicInterfacePlaying() {
        setLayout()
        binding.interfaceSeekStart.text =
            formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
        binding.interfaceSeekEnd.text =
            formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
        binding.seekbar.progress = musicService!!.mediaPlayer!!.currentPosition
        binding.seekbar.max = musicService!!.mediaPlayer!!.duration
        if (isPlaying) {
            binding.interfacePlay.setImageResource((R.drawable.pause))
        } else {
            binding.interfacePlay.setImageResource((R.drawable.play))
        }
        if (isLiked) {
            NowPlaying.binding.fragmentHeartButton.setImageResource(R.drawable.heart_fill)
        } else {
            NowPlaying.binding.fragmentHeartButton.setImageResource(R.drawable.heart)
        }
    }

    private fun initServiceAndPlaylist(
        playlist: ArrayList<MusicClass>, shuffle: Boolean
    ) {
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        musicList = ArrayList()
        musicList.addAll(playlist)
        if (shuffle) musicList.shuffle()
        setLayout()
    }
    private fun setLayout() {
        try {
            Glide.with(this).load(getImageArt(musicList[songPosition].coverArtUrl)).apply(
                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
            ).into(binding.interfaceCover)

            binding.interfaceSongName.text = musicList[songPosition].name
            binding.interfaceArtistName.text = musicList[songPosition].artist

        }catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        try {
            Toast.makeText(this, "++++++++++++", Toast.LENGTH_SHORT).show()
            musicService!!.audioManager.requestAudioFocus(
                musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
            )
            isPlaying = true
            musicService!!.mediaPlayer!!.start()
            binding.interfacePlay.setImageResource((R.drawable.pause))
            musicService!!.showNotification(R.drawable.pause_notification)
            NowPlaying.binding.fragmentButton.setImageResource(R.drawable.pause_now)
        } catch (e: Exception) {
            Toast.makeText(this, "--------", Toast.LENGTH_SHORT).show()
            return
        }
    }
    private fun pauseMusic() {
        try {
            musicService!!.audioManager.abandonAudioFocus(musicService)
            isPlaying = false
            musicService!!.mediaPlayer!!.pause()
            binding.interfacePlay.setImageResource((R.drawable.play))
            musicService!!.showNotification(R.drawable.play_notification)
            NowPlaying.binding.fragmentButton.setImageResource(R.drawable.play_now)
        } catch (e: Exception) {
            return
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(
                musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
            )
        }
        initSong()
        musicService!!.seekBarHandler()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        setLayout()
        initSong()
        counter--

        //for refreshing now playing image & text on song completion
        NowPlaying.binding.fragmentTitle.isSelected = true
        Glide.with(applicationContext).load(getImageArt(musicList[songPosition].coverArtUrl))
            .apply(RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop())
            .into(NowPlaying.binding.fragmentImage)
        NowPlaying.binding.fragmentTitle.text = musicList[songPosition].name
        NowPlaying.binding.fragmentAlbumName.text = musicList[songPosition].artist

    }

    private fun initSong() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList[songPosition].url)
            musicService!!.mediaPlayer!!.prepare()
            binding.interfacePlay.setImageResource((R.drawable.pause))
            binding.interfaceSeekStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.interfaceSeekEnd.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbar.progress = 0
            binding.seekbar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            playMusic()

        } catch (e: Exception) {
            return
        }
    }
    override fun onResume() {
        super.onResume()
        overridePendingTransition(com.google.android.material.R.anim.mtrl_bottom_sheet_slide_in, 0)

    }

}