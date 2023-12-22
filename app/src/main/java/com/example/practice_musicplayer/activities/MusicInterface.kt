package com.example.practice_musicplayer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.databinding.ActivityMusicInterfaceBinding

class MusicInterface : AppCompatActivity() {

    private lateinit var binding:ActivityMusicInterfaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}