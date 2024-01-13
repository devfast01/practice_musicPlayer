package com.example.practice_musicplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.SlidingActivity
import com.example.practice_musicplayer.databinding.ItemLargeCarouselBinding
import com.google.android.material.card.MaterialCardView

class AdapterViewPager  (
    private val context: Context,
    private var musicList: ArrayList<MusicClass>,
    ) :
    RecyclerView.Adapter<AdapterViewPager.MyHolder>() {

    class MyHolder(binding: ItemLargeCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        val songName_Up = binding.mtvItem
        val songName = binding.songName
        val singerName = binding.singerName
        val imgSmall = binding.imgSmall
        val imgLarge = binding.imgLarge
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemLargeCarouselBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    @SuppressLint("CutPasteId", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {

            holder.itemView.findViewById<MaterialCardView>(R.id.smalCard).visibility =
            if (SlidingActivity.statePanel) View.GONE else View.VISIBLE

        holder.songName_Up.text = musicList[position].name
        holder.songName.text = musicList[position].name
        holder.singerName.text = musicList[position].artist

        val optionSmall = RequestOptions()
            .centerCrop()
            .override(100, 100)
        Glide
            .with(context)
            .applyDefaultRequestOptions(optionSmall)
            .load(musicList[position].coverArtUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.image_as_cover)
            .into(holder.imgSmall)

        val otionsLarge = RequestOptions()
            .format(DecodeFormat.PREFER_ARGB_8888) // Use higher quality ARGB_8888 format
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and transformed images
            .override(500, 500) // Load the original image size

        Glide
            .with(context)
            .load(musicList[position].coverArtUrl)
            .apply(otionsLarge)
            .error(R.drawable.image_as_cover)
            .into(holder.imgLarge)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}
