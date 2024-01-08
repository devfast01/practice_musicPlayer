package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.databinding.ActivitySlidingBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout


open class SlidingActivity : AppCompatActivity(){
    private lateinit var binding:ActivitySlidingBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlidingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val slidingLayout: SlidingUpPanelLayout = findViewById(R.id.sliding_layout)
        val text: TextView = findViewById(R.id.text)
        val btnShow: Button = findViewById(R.id.btn_show)
        val btnHide: Button = findViewById(R.id.btn_hide)

        // Set up the Sliding UpPanelLayout
        slidingLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                // Do something when the panel is sliding
                Log.e("panel", "Sliding")
            }

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                // Do something when the panel state changes
                Log.e("panel", "Hide")
            }
        })

        // Show the panel when the "Show" button is clicked
        btnShow.setOnClickListener {
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        // Hide the panel when the "Hide" button is clicked
        btnHide.setOnClickListener {
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

        // Handle clicks on the main content
        text.setOnClickListener {
            // Do something when the main content is clicked
        }
    }

    override fun onResume() {
        super.onResume()
        if (MusicInterface.musicService != null) MainActivity.binding.nowPlaying.visibility = View.VISIBLE
    }
}