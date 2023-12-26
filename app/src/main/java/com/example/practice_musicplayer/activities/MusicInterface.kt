package com.example.practice_musicplayer.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.example.practice_musicplayer.getMainColor


@Suppress("DEPRECATION")
class MusicInterface : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMusicInterfaceBinding
        lateinit var songList: ArrayList<MusicClass>
        lateinit var musicList: ArrayList<MusicClass>
        var musicService: MusicService? = null
        var myService: MyService? = null
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var isRepeating: Boolean = false
        var isShuffling: Boolean = false
        var counter: Int = 0
            set(value) {
                field = kotlin.math.max(value, 0)
            }
        var fIndex: Int = -1
        var isLiked: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
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
        musicList = ArrayList()
        val songList = MainActivity.songList
        musicList.add(songList[songPosition])
        val response = musicList[songPosition]

        Log.e("aglama", "$response")

//        if (intent.data?.scheme.contentEquals("content")) {
//            val intentService = Intent(this, MusicService::class.java)
//            bindService(intentService, this, BIND_AUTO_CREATE)
//            startService(intentService)
//            musicList = ArrayList()
//
//            musicList.add(songList[songPosition])
//            Glide.with(this).load(getImageArt(songList[songPosition].coverArtUrl)).apply(
//                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
//            ).into(binding.interfaceCover)
//            Log.e("IF " ,musicList[songPosition].url)
//            binding.interfaceSongName.text =
//                musicList[songPosition].name.removePrefix("/storage/emulated/0/")
//            binding.interfaceArtistName.text = musicList[songPosition].name
//        } else {
//            Log.e("ELSE " ,intent.getIntExtra("index", 0).toString())
//            initActivity()
//        }

    }

//    private fun getMusicDetails(contentUri: Uri): MusicClass {
//       MainActivity.songList.
//        try {
//
//        }finally {
//
//        }
//    }


    private fun initActivity() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
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

    private fun setLayout() {
        try {
            Glide.with(this).load(getImageArt(musicList[songPosition].coverArtUrl)).apply(
                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
            ).into(binding.interfaceCover)

            binding.interfaceSongName.text = musicList[songPosition].name
            binding.interfaceArtistName.text = musicList[songPosition].artist
            if (isRepeating) {
                binding.interfaceRepeat.setImageResource(R.drawable.repeat_on)
                binding.interfaceRepeat.setColorFilter(ContextCompat.getColor(this, R.color.green))
            }
            if (isShuffling) {
                binding.interfaceShuffle.setColorFilter(ContextCompat.getColor(this, R.color.green))
                binding.interfaceShuffle.setImageResource(R.drawable.shuffle_fill)
            }
            if (isLiked) {
                NowPlaying.binding.fragmentHeartButton.setImageResource(R.drawable.heart_fill)
                binding.interfaceLikeButton.setImageResource(R.drawable.heart_fill)
            } else {
                NowPlaying.binding.fragmentHeartButton.setImageResource(R.drawable.heart)
                binding.interfaceLikeButton.setImageResource(R.drawable.heart)
            }

            val img = getImageArt(musicList[songPosition].coverArtUrl)
            val image = if (img != null) {
                BitmapFactory.decodeByteArray(img, 0, img.size)
            } else {
                BitmapFactory.decodeResource(
                    resources, R.drawable.image_as_cover
                )
            }
            val bgColor = getMainColor(image)
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(0xFFFFFF, bgColor)
            )
            binding.root.background = gradient
            window?.statusBarColor = bgColor
        } catch (e: Exception) {
            return
        }
    }

    private fun initServiceAndPlaylist(
        playlist: ArrayList<MusicClass>, shuffle: Boolean,
    ) {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musicList = ArrayList()
        musicList.addAll(playlist)
        if (shuffle) musicList.shuffle()
        setLayout()
    }


    override fun onCompletion(mp: MediaPlayer?) {

        counter--
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        TODO("Not yet implemented")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }
}