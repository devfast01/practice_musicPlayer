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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MainActivity
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.databinding.FragmentNowPlayingBinding
import com.example.practice_musicplayer.getImageArt
import com.example.practice_musicplayer.getNewSongs
import com.example.practice_musicplayer.setSongPosition
import com.example.practice_musicplayer.utils.OnSwipeTouchListener

@Suppress("DEPRECATION")
class NowPlaying : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        val view: View = binding.root
        binding.root.visibility = View.GONE

        binding.fragmentButton.setOnClickListener {
            if (MusicInterface.isPlaying) pauseMusic()
            else playMusic()
        }


        // Inflate the layout for this fragment
        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        if (MusicInterface.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.fragmentTitle.isSelected = true

            binding.root.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
                override fun onSingleClick() {
                    openActivity()
                }

                override fun onSwipeTop() {
                    Log.d(ContentValues.TAG, "onSwipeTop: Performed")
                    openActivity()
                }

                override fun onSwipeLeft() {
                    nextPrevMusic(increment = true)
                }

                override fun onSwipeRight() {
                    nextPrevMusic(increment = false)
                }
            })
            Log.e("musiclist", MusicInterface.musicList.toString())
            Glide.with(this)
                .load(MusicInterface.musicList[MusicInterface.songPosition].coverArtUrl)
                .apply(
                    RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
                ).into(binding.fragmentImage)
            binding.fragmentTitle.text =
                MusicInterface.musicList[MusicInterface.songPosition].name
            binding.fragmentAlbumName.text =
                MusicInterface.musicList[MusicInterface.songPosition].artist
            if (MusicInterface.isPlaying) binding.fragmentButton.setImageResource(R.drawable.pause_now)
            else binding.fragmentButton.setImageResource(R.drawable.play_now)

        }
    }

    private fun playMusic() {
        MusicInterface.musicService!!.audioManager.requestAudioFocus(
            MusicInterface.musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        )
        MusicInterface.isPlaying = true
        binding.fragmentButton.setImageResource(R.drawable.pause_now)
        MusicInterface.musicService!!.showNotification(R.drawable.pause_notification)
        MusicInterface.binding.interfacePlay.setImageResource(R.drawable.pause)
        MusicInterface.musicService!!.mediaPlayer!!.start()

    }

    private fun pauseMusic() {
        MusicInterface.musicService!!.audioManager.abandonAudioFocus(MusicInterface.musicService)
        MusicInterface.isPlaying = false
        MusicInterface.musicService!!.mediaPlayer!!.pause()
        MusicInterface.musicService!!.showNotification(R.drawable.play_notification)
        MusicInterface.binding.interfacePlay.setImageResource(R.drawable.play)
        binding.fragmentButton.setImageResource(R.drawable.play_now)

    }

    private fun nextPrevMusic(increment: Boolean) {
        setSongPosition(increment = increment)
        MusicInterface.musicService!!.initSong()
        Glide.with(requireContext())
            .load(getImageArt(MusicInterface.musicList!![MusicInterface.songPosition].coverArtUrl))
            .apply(
                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
            ).into(binding.fragmentImage)
        binding.fragmentTitle.text =
            MusicInterface.musicList[MusicInterface.songPosition].name
        binding.fragmentAlbumName.text =
            MusicInterface.musicList[MusicInterface.songPosition].artist
        playMusic()
    }

    fun openActivity() {
        val intent = Intent(requireContext(), MusicInterface::class.java)
        intent.putExtra("index", MusicInterface.songPosition)
        intent.putExtra("class", "Now playing")
        ContextCompat.startActivity(requireContext(), intent, null)
    }

}