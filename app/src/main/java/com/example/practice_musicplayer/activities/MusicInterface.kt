package com.example.practice_musicplayer.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
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
import com.example.practice_musicplayer.exitApplication
import com.example.practice_musicplayer.formatDuration
import com.example.practice_musicplayer.fragments.NowPlaying
import com.example.practice_musicplayer.getImageArt
import com.example.practice_musicplayer.getMainColor
import com.example.practice_musicplayer.setSongPosition
import com.example.practice_musicplayer.utils.OnSwipeTouchListener
import com.google.android.material.snackbar.Snackbar


@Suppress("DEPRECATION")
class MusicInterface : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMusicInterfaceBinding
        var songList: ArrayList<MusicClass>? = null
        var musicList: ArrayList<MusicClass>? = null
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.interfaceSongName.isSelected = true

        binding.backButton.setOnClickListener {
            finish()
        }
//        musicList = ArrayList()
//        val songList = MainActivity.songList
//        musicList.add(songList[songPosition])
//        val response = musicList[songPosition]

        Log.e("aglama", intent.getStringExtra("class").toString())

        if (intent.data?.scheme.contentEquals("content")) {
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicList = ArrayList()
            musicList!!.add(songList!![songPosition])
            Glide.with(this).load(getImageArt(musicList!![songPosition].coverArtUrl)).apply(
                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
            ).into(binding.interfaceCover)
            Log.e("IF ", musicList!![songPosition].url)
            binding.interfaceSongName.text =
                musicList!![songPosition].name
            binding.interfaceArtistName.text = musicList!![songPosition].artist
        } else {
            Log.e("ELSE ", intent.getIntExtra("index", 0).toString())
            initActivity()
        }

        binding.interfacePlay.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }

        binding.interfaceNext.setOnClickListener {
            prevNextSong(increment = true)
        }
        binding.interfacePrevious.setOnClickListener {
            prevNextSong(increment = false)
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, isUser: Boolean) {
                try {

                    if (isUser) {
                        musicService!!.mediaPlayer!!.seekTo(progress)
                        musicService!!.showNotification(if (isPlaying) R.drawable.pause_notification else R.drawable.play_notification)
                    }
                } catch (e: Exception) {
                    return
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })

        binding.interfaceEqualizer.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 3)
            } catch (e: Exception) {
                Snackbar.make(
                    this, it, "Equalizer feature not supported in your device.", 3000
                ).show()
            }
        }

        binding.interfaceCover.setOnTouchListener(object : OnSwipeTouchListener(baseContext) {
            override fun onSingleClick() {
                if (isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }

            override fun onSwipeDown() {
                Log.d(ContentValues.TAG, "onSwipeDown: Performed")
                finish()
            }

            override fun onSwipeLeft() {
                prevNextSong(increment = true)
            }

            override fun onSwipeRight() {
                prevNextSong(increment = false)
            }
        })

        binding.root.setOnTouchListener(object : OnSwipeTouchListener(baseContext) {

            override fun onSwipeDown() {
                Log.d(ContentValues.TAG, "onSwipeDown: Performed")
                finish()
            }

            override fun onSwipeLeft() {
                prevNextSong(increment = true)
            }

            override fun onSwipeRight() {
                prevNextSong(increment = false)
            }
        })


    }

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

        }
    }

    private fun setLayout() {
        try {
            Glide.with(this).load(getImageArt(musicList!![songPosition].coverArtUrl)).apply(
                RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop()
            ).into(binding.interfaceCover)

            binding.interfaceSongName.text = musicList!![songPosition].name
            binding.interfaceArtistName.text = musicList!![songPosition].artist
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

            val img = getImageArt(musicList!![songPosition].coverArtUrl)
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

    private fun initSong() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList!![songPosition].url)
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

    private fun playMusic() {
        try {
            musicService!!.audioManager.requestAudioFocus(
                musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
            )
            isPlaying = true
            musicService!!.mediaPlayer!!.start()
            binding.interfacePlay.setImageResource((R.drawable.pause))
            musicService!!.showNotification(R.drawable.pause_notification)
            NowPlaying.binding.fragmentButton.setImageResource(R.drawable.pause_now)
        } catch (e: Exception) {
            return
        }
    }

    fun pauseMusic() {
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

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            initSong()
            counter--
        } else {
            setSongPosition(increment = false)
            setLayout()
            initSong()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show()
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

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
        Toast.makeText(this, "disconnected", Toast.LENGTH_SHORT).show()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true)
        setLayout()
        initSong()
        counter--

        //for refreshing now playing image & text on song completion
        NowPlaying.binding.fragmentTitle.isSelected = true
        Glide.with(applicationContext).load(getImageArt(musicList!![songPosition].url))
            .apply(RequestOptions().placeholder(R.drawable.image_as_cover).centerCrop())
            .into(NowPlaying.binding.fragmentImage)
        NowPlaying.binding.fragmentTitle.text = musicList!![songPosition].name
        NowPlaying.binding.fragmentAlbumName.text = musicList!![songPosition].artist

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 || resultCode == RESULT_OK) return

    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicList!![songPosition].id != 0 && !isPlaying) exitApplication()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, com.google.android.material.R.anim.mtrl_bottom_sheet_slide_out)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(com.google.android.material.R.anim.mtrl_bottom_sheet_slide_in, 0)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            showMusicInterfacePlaying()
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

    }


    fun initServiceAndPlaylist(
        playlist: ArrayList<MusicClass>, shuffle: Boolean,
    ) {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musicList = ArrayList()
        musicList!!.addAll(playlist)
//        if (shuffle) musicList!!.shuffle()
//        setLayout()
    }

}