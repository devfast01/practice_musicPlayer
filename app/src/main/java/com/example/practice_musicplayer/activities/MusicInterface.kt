package com.example.practice_musicplayer.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MainActivity
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.MusicService
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.databinding.ActivityMusicInterfaceBinding
import com.example.practice_musicplayer.getImageArt

class MusicInterface : AppCompatActivity(), ServiceConnection {

    companion object {
        lateinit var musicList: ArrayList<MusicClass>
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

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMusicInterfaceBinding
        var musicService: MusicService? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicInterfaceBinding.inflate(layoutInflater)
        setTheme(R.style.Base_Theme_Practice_musicPlayer)
        setContentView(binding.root)
        binding.interfaceSongName.isSelected = true

        Log.e("Start", "Starting")

        if (intent.data?.scheme.contentEquals("content")) {
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicList = ArrayList()
            musicList.add(getMusicDetails(intent.data!!))
            Glide.with(this).load(getImageArt(musicList[songPosition].path)).apply(
                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
            ).into(binding.interfaceCover)
            binding.interfaceSongName.text =
                musicList[songPosition].title.removePrefix("/storage/emulated/0/")
            binding.interfaceArtistName.text = musicList[songPosition].album
        } else {
            initActivity()
        }
    }

    private fun initActivity() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                initServiceAndPlaylist(MainActivity.songList, shuffle = false)
            }
        }
    }

    private fun getMusicDetails(contentUri: Uri): MusicClass {
        var cursor: Cursor? = null

        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }!!
            return MusicClass(
                id = "Unknown",
                title = path.toString(),
                album = "Unknown",
                artist = "Unknown",
                length = duration,
                artUri = "Unknown",
                path = path.toString()
            )
        } finally {
            cursor?.close()
        }
    }

    private fun initServiceAndPlaylist(
        playlist: ArrayList<MusicClass>, shuffle: Boolean
    ) {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musicList = ArrayList()
        musicList.addAll(playlist)
        if (shuffle) musicList.shuffle()
        setLayout()
    }

    private fun setLayout() {
        try {

        }catch (e: Exception) {
            return
        }
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        TODO("Not yet implemented")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        TODO("Not yet implemented")
    }
}