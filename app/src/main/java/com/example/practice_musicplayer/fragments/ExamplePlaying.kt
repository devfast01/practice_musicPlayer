package com.example.practice_musicplayer.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.databinding.FragmentExamplePlayingBinding
import com.example.practice_musicplayer.databinding.FragmentNowPlayingBinding
import com.example.practice_musicplayer.getImageArt
import com.example.practice_musicplayer.setSongPosition
import com.example.practice_musicplayer.utils.OnSwipeTouchListener


@Suppress("DEPRECATION")
class ExamplePlaying : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentExamplePlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentExamplePlayingBinding.inflate(inflater, container, false)
        val view: View = binding.root
        binding.root.visibility = View.VISIBLE

        binding.playButton.setOnClickListener {
            Toast.makeText(requireContext(), "Play clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
    }

}