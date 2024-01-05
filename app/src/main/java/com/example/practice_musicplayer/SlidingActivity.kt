package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_musicplayer.databinding.ActivitySlidingBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout


open class SlidingActivity : AppCompatActivity(){
    private lateinit var binding:ActivitySlidingBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlidingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set gravity programmatically
        binding.slidingLayout.panelGravity = SlidingUpPanelLayout.PanelGravity.BOTTOM // or SlidingUpPanelLayout.PanelGravity.TOP


        // Optional: Set up sliding panel listener to handle panel state changes
        binding.slidingLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {

                // Panel is sliding
            }

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                // Panel state changed
            }
        })
    }

    // Optional: Method to toggle the sliding panel
    fun togglePanel(view: View) {
        val slidingLayout: SlidingUpPanelLayout = findViewById(R.id.sliding_layout)
        slidingLayout.panelState = if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            SlidingUpPanelLayout.PanelState.EXPANDED
        }
    }

}