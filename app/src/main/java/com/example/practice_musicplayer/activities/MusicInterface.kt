package com.example.practice_musicplayer.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MainActivity
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.MusicService
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.adapters.MusicAdapter
import com.example.practice_musicplayer.databinding.ActivityMusicInterfaceBinding
import com.example.practice_musicplayer.getImageArt
import com.example.practice_musicplayer.utils.RetrofitService
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicInterface : AppCompatActivity() {

    private val apiService = RetrofitService.getInstance()
    private lateinit var musicAdapter: MusicAdapter
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.interfaceSongName.isSelected = true

        initActivity()

    }

    private fun initActivity() {
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                Log.d("begli", MainActivity.songList.toString())
                initServiceAndPlaylist(MainActivity.songList, shuffle = false)
            }
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

    }

}