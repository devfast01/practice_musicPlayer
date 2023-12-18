package com.example.practice_musicplayer.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practice_musicplayer.MainActivity
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.adapters.FavouriteAdapter
import com.example.practice_musicplayer.checkPlaylist
import com.example.practice_musicplayer.databinding.ActivityFavouriteBinding
import java.io.File

class FavouriteActivity : AppCompatActivity() {
    private lateinit var adapter : FavouriteAdapter
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityFavouriteBinding
        var favSongList: ArrayList<MusicClass> = ArrayList()
        lateinit var musicListSearch: java.util.ArrayList<MusicClass>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (favSongList.isNotEmpty()) {
            setTheme(R.style.Base_Theme_Practice_musicPlayer)
        }

        favSongList = checkPlaylist(favSongList)

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in favSongList)
                        if (song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    MainActivity.isSearching = true
                    adapter.updateMusicList(searchList = musicListSearch)

                }
                return true
            }

        })

        val recyclerView = binding.listViewFA
        adapter = FavouriteAdapter(this, favSongList)
        recyclerView.adapter = adapter
        recyclerView.setItemViewCacheSize(50)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(this@FavouriteActivity)
        if (favSongList.size < 1) binding.floatingActionButton.visibility = View.INVISIBLE
        binding.floatingActionButton.setOnClickListener {
            shuffleSongs()
        }
    }
    private fun shuffleSongs() {
        val intent = Intent(this, MusicInterface::class.java)
        intent.putExtra("index", 0)
        intent.putExtra("class", "FavouriteShuffle")
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}