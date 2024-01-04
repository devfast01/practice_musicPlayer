package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_musicplayer.databinding.ActivitySlidingBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout


open class SlidingActivity : AppCompatActivity(), SlidingUpPanelLayout.PanelSlideListener {
    private lateinit var binding:ActivitySlidingBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlidingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slidingLayout: SlidingUpPanelLayout = findViewById(R.id.sliding_layout)

        slidingLayout.addPanelSlideListener(this)
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        // Called while the Panel is sliding.
        // You can use the slideOffset to perform animations or update UI elements.
    }

    override fun onPanelStateChanged(
        panel: View?,
        previousState: SlidingUpPanelLayout.PanelState?,
        newState: SlidingUpPanelLayout.PanelState?
    ) {
        // Called when the Panel's state changes (e.g., collapsed, expanded, hidden).
        // You can perform actions based on the new state.
    }

}