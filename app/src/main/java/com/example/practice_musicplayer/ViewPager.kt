package com.example.practice_musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practice_musicplayer.adapters.AdapterCarousel
import com.example.practice_musicplayer.databinding.ActivityViewPagerBinding
import com.example.practice_musicplayer.utils.DpData
import com.example.practice_musicplayer.utils.ViewPagerExtensions.addCarouselEffect

class ViewPager : AppCompatActivity() {
    private val binding by lazy { ActivityViewPagerBinding.inflate(layoutInflater) }
    private val adapter by lazy { AdapterCarousel() }
    private val dpData by lazy { DpData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViewPager()
    }

    private fun initViewPager() {
        binding.viewPager.addCarouselEffect()
        binding.viewPager.adapter = adapter
        adapter.submitList(dpData.dummyData)
    }
}